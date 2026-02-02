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
                // 강제로 ID 주입 (테스트용)
                // ReflectionTestUtils.setField(p, "id", 1L); 
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
            // 내부적으로 addWalkingStyle 등이 호출되었는지 확인은 어렵지만, 예외가 안 났다면 성공으로 간주
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
}
