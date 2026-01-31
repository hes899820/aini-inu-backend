package scit.ainiinu.pet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scit.ainiinu.pet.dto.response.BreedResponse;
import scit.ainiinu.pet.dto.response.PersonalityResponse;
import scit.ainiinu.common.exception.BusinessException;
import scit.ainiinu.pet.dto.request.PetCreateRequest;
import scit.ainiinu.pet.dto.response.PetResponse;
import scit.ainiinu.pet.dto.response.WalkingStyleResponse;
import scit.ainiinu.pet.entity.Breed;
import scit.ainiinu.pet.entity.Personality;
import scit.ainiinu.pet.entity.Pet;
import scit.ainiinu.pet.entity.WalkingStyle;
import scit.ainiinu.pet.exception.PetErrorCode;
import scit.ainiinu.pet.repository.BreedRepository;
import scit.ainiinu.pet.repository.PersonalityRepository;
import scit.ainiinu.pet.repository.PetRepository;
import scit.ainiinu.pet.repository.WalkingStyleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository petRepository;
    private final BreedRepository breedRepository;
    private final PersonalityRepository personalityRepository;
    private final WalkingStyleRepository walkingStyleRepository;

    /**
     * 반려견 등록
     */
    @Transactional
    public PetResponse createPet(Long memberId, PetCreateRequest request) {
        // 1. 등록 가능 마릿수(10마리) 확인
        int currentCount = petRepository.countByMemberId(memberId);
        if (currentCount >= 10) {
            throw new BusinessException(PetErrorCode.PET_LIMIT_EXCEEDED);
        }

        // 2. 견종 조회
        Breed breed = breedRepository.findById(request.getBreedId())
                .orElseThrow(() -> new BusinessException(PetErrorCode.BREED_NOT_FOUND));

        // 3. 메인 반려견 설정 로직
        // 첫 등록이면 무조건 메인, 아니면 요청값 따르되 기본은 false
        boolean isMain = false;
        if (currentCount == 0) {
            isMain = true;
        } else if (request.getIsMain() != null && request.getIsMain()) {
            // 이미 메인이 있는데 새로 메인으로 등록하려는 경우, 기존 메인 해제 필요
            petRepository.findByMemberIdAndIsMainTrue(memberId)
                    .ifPresent(mainPet -> mainPet.setMain(false));
            isMain = true;
        }

        // 4. Pet 엔티티 생성
        Pet pet = Pet.builder()
                .memberId(memberId)
                .breed(breed)
                .name(request.getName())
                .age(request.getAge())
                .gender(request.getGender())
                .size(request.getSize())
                .mbti(request.getMbti())
                .isNeutered(request.getIsNeutered())
                .photoUrl(request.getPhotoUrl())
                .isMain(isMain)
                .certificationNumber(request.getCertificationNumber())
                .isCertified(false) // 초기엔 미인증, 추후 검증 API 통해 true로 변경
                .build();

        // 5. 성향(Personality) 관계 설정
        if (request.getPersonalityIds() != null) {
            for (Long pId : request.getPersonalityIds()) {
                Personality personality = personalityRepository.findById(pId)
                        .orElseThrow(() -> new BusinessException(PetErrorCode.PERSONALITY_NOT_FOUND));
                pet.addPersonality(personality);
            }
        }

        // 6. 산책 스타일(WalkingStyle) 관계 설정
        if (request.getWalkingStyles() != null && !request.getWalkingStyles().isEmpty()) {
            List<WalkingStyle> styles = walkingStyleRepository.findByCodeIn(request.getWalkingStyles());
            
            // 요청한 코드 수와 조회된 스타일 수가 다르면 유효하지 않은 코드가 포함된 것임
            // (중복 코드가 요청에 없다는 가정 하에, 혹은 Set으로 변환하여 비교)
            if (styles.size() != request.getWalkingStyles().size()) {
                 throw new BusinessException(PetErrorCode.INVALID_PET_INFO);
            }
            
            for (WalkingStyle style : styles) {
                pet.addWalkingStyle(style);
            }
        }

        // 7. 저장
        Pet savedPet = petRepository.save(pet);

        // TODO: 회원의 memberType을 PET_OWNER로 변경하는 로직 필요 (MemberService)

        return toResponse(savedPet);
    }

    /**
     * 전체 견종 목록 조회
     */
    public List<BreedResponse> getAllBreeds() {
        return breedRepository.findAll().stream()
                .map(BreedResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 전체 성격 목록 조회
     */
    public List<PersonalityResponse> getAllPersonalities() {
        return personalityRepository.findAll().stream()
                .map(PersonalityResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 전체 산책 스타일 목록 조회
     */
    public List<WalkingStyleResponse> getAllWalkingStyles() {
        return walkingStyleRepository.findAll().stream()
                .map(WalkingStyleResponse::from)
                .collect(Collectors.toList());
    }
    
    private PetResponse toResponse(Pet pet) {
        List<String> walkingStyleCodes = pet.getPetWalkingStyles().stream()
                .map(pws -> pws.getWalkingStyle().getCode())
                .collect(Collectors.toList());
        
        List<PersonalityResponse> personalityResponses = pet.getPetPersonalities().stream()
                .map(pp -> PersonalityResponse.from(pp.getPersonality()))
                .collect(Collectors.toList());

        return PetResponse.from(pet, walkingStyleCodes, personalityResponses);
    }
}