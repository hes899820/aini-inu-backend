package scit.ainiinu.pet.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import scit.ainiinu.common.exception.BusinessException;
import scit.ainiinu.pet.dto.request.PetCreateRequest;
import scit.ainiinu.pet.dto.request.PetUpdateRequest;
import scit.ainiinu.pet.dto.response.PetResponse;
import scit.ainiinu.pet.entity.Breed;
import scit.ainiinu.pet.entity.Personality;
import scit.ainiinu.pet.entity.Pet;
import scit.ainiinu.pet.entity.WalkingStyle;
import scit.ainiinu.pet.entity.enums.PetGender;
import scit.ainiinu.pet.entity.enums.PetSize;
import scit.ainiinu.pet.exception.PetErrorCode;
import scit.ainiinu.pet.repository.BreedRepository;
import scit.ainiinu.pet.repository.PersonalityRepository;
import scit.ainiinu.pet.repository.PetRepository;
import scit.ainiinu.pet.repository.WalkingStyleRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @InjectMocks
    private PetService petService;

    @Mock
    private PetRepository petRepository;
    @Mock
    private BreedRepository breedRepository;
    @Mock
    private PersonalityRepository personalityRepository;
    @Mock
    private WalkingStyleRepository walkingStyleRepository;
    @Mock
    private AnimalCertificationService animalCertificationService;

    @Nested
    @DisplayName("반려견 등록")
    class CreatePet {

        @Test
        @DisplayName("성공: 동물등록번호 검증이 성공하면 isCertified=true로 저장된다")
        void success_certification_verified() {
            // given
            Long memberId = 1L;
            PetCreateRequest request = createRequest();
            request.setCertificationNumber("123456789012345");

            given(petRepository.countByMemberId(memberId)).willReturn(0);
            given(breedRepository.findById(request.getBreedId())).willReturn(Optional.of(mock(Breed.class)));
            given(animalCertificationService.verify(request.getCertificationNumber())).willReturn(true); // 검증 성공
            
            // save 호출 시 전달된 Pet 객체를 캡처하여 검증해도 되지만, 여기서는 반환값으로 확인
            given(petRepository.save(any(Pet.class))).willAnswer(invocation -> {
                Pet p = invocation.getArgument(0);
                return p;
            });

            // when
            PetResponse response = petService.createPet(memberId, request);

            // then
            assertThat(response.getIsCertified()).isTrue();
            then(animalCertificationService).should().verify(request.getCertificationNumber());
        }

        @Test
        @DisplayName("성공: 첫 반려견 등록 시 자동으로 메인 반려견이 된다")
        void success_first_pet_auto_main() {
            // given
            Long memberId = 1L;
            PetCreateRequest request = createRequest();

            given(petRepository.countByMemberId(memberId)).willReturn(0); // 0마리
            given(breedRepository.findById(request.getBreedId())).willReturn(Optional.of(mock(Breed.class)));
            given(petRepository.save(any(Pet.class))).willAnswer(invocation -> invocation.getArgument(0)); // 저장된 객체 반환

            // when
            PetResponse response = petService.createPet(memberId, request);

            // then
            assertThat(response.getIsMain()).isTrue();
            then(petRepository).should(times(1)).save(any(Pet.class));
        }

        @Test
        @DisplayName("성공: 이미 반려견이 있을 때 isMain=true로 등록하면 기존 메인이 해제된다")
        void success_change_main_pet() {
            // given
            Long memberId = 1L;
            PetCreateRequest request = createRequest();
            request.setIsMain(true);

            Pet existingMainPet = mock(Pet.class);

            given(petRepository.countByMemberId(memberId)).willReturn(1);
            given(breedRepository.findById(request.getBreedId())).willReturn(Optional.of(mock(Breed.class)));
            given(petRepository.findByMemberIdAndIsMainTrue(memberId)).willReturn(Optional.of(existingMainPet));
            given(petRepository.save(any(Pet.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            PetResponse response = petService.createPet(memberId, request);

            // then
            assertThat(response.getIsMain()).isTrue();
            then(existingMainPet).should(times(1)).setMain(false); // 기존 메인 해제 검증
        }

        @Test
        @DisplayName("성공: 산책 스타일과 성향을 포함하여 등록한다")
        void success_with_styles_and_personalities() {
            // given
            Long memberId = 1L;
            PetCreateRequest request = createRequest();
            request.setWalkingStyles(List.of("RUN", "SNIFF"));
            request.setPersonalityIds(List.of(1L));

            WalkingStyle style1 = mock(WalkingStyle.class);
            WalkingStyle style2 = mock(WalkingStyle.class);
            
            // walkingStyleRepository 반환값 설정
            given(petRepository.countByMemberId(memberId)).willReturn(0);
            given(breedRepository.findById(any())).willReturn(Optional.of(mock(Breed.class)));
            given(walkingStyleRepository.findByCodeIn(anyList())).willReturn(List.of(style1, style2)); // 2개 반환
            given(personalityRepository.findById(1L)).willReturn(Optional.of(mock(Personality.class)));
            given(petRepository.save(any(Pet.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            PetResponse response = petService.createPet(memberId, request);

            // then
            then(walkingStyleRepository).should().findByCodeIn(request.getWalkingStyles());
            then(personalityRepository).should().findById(1L);
        }

        @Test
        @DisplayName("실패: 등록 가능 마릿수(10마리)를 초과하면 예외 발생 (P002)")
        void fail_limit_exceeded() {
            // given
            given(petRepository.countByMemberId(1L)).willReturn(10); // 이미 10마리

            // when & then
            assertThatThrownBy(() -> petService.createPet(1L, createRequest()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PetErrorCode.PET_LIMIT_EXCEEDED);
        }

        @Test
        @DisplayName("실패: 유효하지 않은 산책 스타일 코드가 있으면 예외 발생")
        void fail_invalid_walking_style() {
            // given
            PetCreateRequest request = createRequest();
            request.setWalkingStyles(List.of("INVALID", "VALID"));

            given(petRepository.countByMemberId(1L)).willReturn(0);
            given(breedRepository.findById(any())).willReturn(Optional.of(mock(Breed.class)));
            // DB에는 1개만 존재한다고 가정 (하나가 잘못됨)
            given(walkingStyleRepository.findByCodeIn(anyList())).willReturn(List.of(mock(WalkingStyle.class)));

            // when & then
            assertThatThrownBy(() -> petService.createPet(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PetErrorCode.INVALID_PET_INFO);
        }

        private PetCreateRequest createRequest() {
            PetCreateRequest req = new PetCreateRequest();
            req.setName("Mong");
            req.setBreedId(1L);
            req.setAge(3);
            req.setGender(PetGender.MALE);
            req.setSize(PetSize.SMALL);
            req.setIsNeutered(true);
            req.setPhotoUrl("url");
            req.setIsMain(false);
            return req;
        }
    }

    @Nested
    @DisplayName("회원 반려견 목록 조회")
    class GetUserPets {

        @Test
        @DisplayName("성공: 회원의 반려견 목록을 조회한다")
        void success_get_user_pets() {
            // given
            Long memberId = 1L;
            
            // Mock Pets
            Breed breed = mock(Breed.class);
            Pet mainPet = Pet.builder()
                    .memberId(memberId)
                    .breed(breed)
                    .name("MainDog")
                    .age(5)
                    .gender(PetGender.MALE)
                    .size(PetSize.SMALL)
                    .isMain(true)
                    .build();

            Pet subPet = Pet.builder()
                    .memberId(memberId)
                    .breed(breed)
                    .name("SubDog")
                    .age(2)
                    .gender(PetGender.FEMALE)
                    .size(PetSize.MEDIUM)
                    .isMain(false)
                    .build();

            given(petRepository.findAllByMemberIdOrderByIsMainDesc(memberId))
                    .willReturn(List.of(mainPet, subPet));

            // when
            List<PetResponse> responses = petService.getUserPets(memberId);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getName()).isEqualTo("MainDog");
            assertThat(responses.get(0).getIsMain()).isTrue();
            assertThat(responses.get(1).getName()).isEqualTo("SubDog");
            assertThat(responses.get(1).getIsMain()).isFalse();

            then(petRepository).should(times(1)).findAllByMemberIdOrderByIsMainDesc(memberId);
        }
    }

    @Nested
    @DisplayName("반려견 정보 수정")
    class UpdatePet {

        @Test
        @DisplayName("성공: 반려견 기본 정보와 관계 정보를 수정한다")
        void success_update_pet() {
            // given
            Long memberId = 1L;
            Long petId = 100L;
            PetUpdateRequest request = updateRequest();
            
            // Real Pet Object
            Breed breed = mock(Breed.class);
            Pet pet = Pet.builder()
                    .id(petId)
                    .memberId(memberId)
                    .breed(breed)
                    .name("OldName")
                    .age(2)
                    .gender(PetGender.MALE)
                    .size(PetSize.SMALL)
                    .isNeutered(false)
                    .isMain(true)
                    .build();
            
            // Spy to verify method calls
            Pet existingPet = org.mockito.Mockito.spy(pet);
            
            given(petRepository.findById(petId)).willReturn(Optional.of(existingPet));
            
            // Mocking Relations
            given(personalityRepository.findById(1L)).willReturn(Optional.of(mock(Personality.class)));
            given(walkingStyleRepository.findByCodeIn(anyList())).willReturn(List.of(mock(WalkingStyle.class)));

            // when
            petService.updatePet(memberId, petId, request);

            // then
            // 1. 상태 변경 확인 (Dirty Checking 유발 확인)
            assertThat(existingPet.getName()).isEqualTo(request.getName());
            assertThat(existingPet.getAge()).isEqualTo(request.getAge());
            assertThat(existingPet.getIsNeutered()).isEqualTo(request.getIsNeutered());

            // 2. 메서드 호출 검증
            then(existingPet).should().updateBasicInfo(any(), any(), any(), any(), any());
            then(existingPet).should().clearPersonalities();
            then(existingPet).should().clearWalkingStyles();
        }

        @Test
        @DisplayName("실패: 본인의 반려견이 아니면 수정할 수 없다 (P006)")
        void fail_not_owner() {
            // given
            Long memberId = 1L;
            Long otherMemberId = 2L;
            Long petId = 100L;
            PetUpdateRequest request = updateRequest();

            Pet pet = mock(Pet.class);
            given(pet.getMemberId()).willReturn(otherMemberId); // 다른 사람 소유
            given(petRepository.findById(petId)).willReturn(Optional.of(pet));

            // when & then
            assertThatThrownBy(() -> petService.updatePet(memberId, petId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PetErrorCode.NOT_YOUR_PET);
        }

        private PetUpdateRequest updateRequest() {
            return PetUpdateRequest.builder()
                    .name("UpdatedName")
                    .age(5)
                    .isNeutered(true)
                    .mbti("INTJ")
                    .photoUrl("new_url")
                    .personalityIds(List.of(1L))
                    .walkingStyleCodes(List.of("RUN"))
                    .build();
        }
    }

    @Nested
    @DisplayName("반려견 삭제")
    class DeletePet {

        @Test
        @DisplayName("성공: 메인 반려견을 삭제하면 남은 아이 중 하나가 메인이 된다")
        void success_delete_main_pet_promote_other() {
            // given
            Long memberId = 1L;
            Long petId = 100L;
            Pet mainPet = mock(Pet.class);
            Pet otherPet = mock(Pet.class);

            given(mainPet.getMemberId()).willReturn(memberId);
            given(mainPet.getIsMain()).willReturn(true); // 삭제될 아이가 메인임

            given(petRepository.findById(petId)).willReturn(Optional.of(mainPet));
            // 삭제 후 남은 목록에 otherPet이 있다고 가정
            given(petRepository.findAllByMemberIdOrderByIsMainDesc(memberId)).willReturn(List.of(otherPet));

            // when
            petService.deletePet(memberId, petId);

            // then
            then(petRepository).should().delete(mainPet);
            then(otherPet).should().setMain(true); // 다른 아이가 메인으로 승격되었는지 확인
        }

        @Test
        @DisplayName("성공: 마지막 반려견을 삭제하면 아무것도 남지 않는다")
        void success_delete_last_pet() {
            // given
            Long memberId = 1L;
            Long petId = 100L;
            Pet pet = mock(Pet.class);

            given(pet.getMemberId()).willReturn(memberId);
            given(pet.getIsMain()).willReturn(true);

            given(petRepository.findById(petId)).willReturn(Optional.of(pet));
            given(petRepository.findAllByMemberIdOrderByIsMainDesc(memberId)).willReturn(List.of()); // 남은 아이 없음

            // when
            petService.deletePet(memberId, petId);

            // then
            then(petRepository).should().delete(pet);
        }

        @Test
        @DisplayName("실패: 본인의 반려견이 아니면 삭제할 수 없다")
        void fail_delete_not_owner() {
            // given
            Long memberId = 1L;
            Long otherMemberId = 2L;
            Long petId = 100L;
            Pet pet = mock(Pet.class);
            given(pet.getMemberId()).willReturn(otherMemberId);
            given(petRepository.findById(petId)).willReturn(Optional.of(pet));

            // when & then
            assertThatThrownBy(() -> petService.deletePet(memberId, petId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", PetErrorCode.NOT_YOUR_PET);
        }
    }
}
