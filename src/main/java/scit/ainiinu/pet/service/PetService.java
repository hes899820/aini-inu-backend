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
    private final AnimalCertificationService animalCertificationService;

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

        // 3. 동물등록번호 검증 (선택 사항)
        boolean isCertified = animalCertificationService.verify(request.getCertificationNumber());

        // 4. 메인 반려견 설정 로직
        boolean isMain = false;
        if (currentCount == 0) {
            isMain = true;
        } else if (request.getIsMain() != null && request.getIsMain()) {
            petRepository.findByMemberIdAndIsMainTrue(memberId)
                    .ifPresent(mainPet -> mainPet.setMain(false));
            isMain = true;
        }

        // 5. Pet 엔티티 생성
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
                .isCertified(isCertified)
                .build();

        // 6. 성향(Personality) 및 산책 스타일 관계 설정
        if (request.getPersonalityIds() != null) {
            for (Long pId : request.getPersonalityIds()) {
                Personality personality = personalityRepository.findById(pId)
                        .orElseThrow(() -> new BusinessException(PetErrorCode.PERSONALITY_NOT_FOUND));
                pet.addPersonality(personality);
            }
        }

        if (request.getWalkingStyles() != null && !request.getWalkingStyles().isEmpty()) {
            List<WalkingStyle> styles = walkingStyleRepository.findByCodeIn(request.getWalkingStyles());
            if (styles.size() != request.getWalkingStyles().size()) {
                 throw new BusinessException(PetErrorCode.INVALID_PET_INFO);
            }
            styles.forEach(pet::addWalkingStyle);
        }

        // 7. 저장
        Pet savedPet = petRepository.save(pet);

        // TODO:  MemberService.updateMemberType(memberId, PET_OWNER) 호출 필요
        // 회원 타입 자동 전환: 첫 반려견 등록 시 회원의 memberType이 NON_PET_OWNER -> PET_OWNER로 변경되어야 함.

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