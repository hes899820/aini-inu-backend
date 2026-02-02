package scit.ainiinu.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import scit.ainiinu.member.dto.request.MemberCreateRequest;
import scit.ainiinu.member.dto.response.MemberResponse;
import scit.ainiinu.member.entity.Member;
import scit.ainiinu.member.entity.MemberPersonalityType;
import scit.ainiinu.member.entity.enums.Gender;
import scit.ainiinu.member.exception.MemberException;
import scit.ainiinu.member.repository.MemberPersonalityRepository;
import scit.ainiinu.member.repository.MemberPersonalityTypeRepository;
import scit.ainiinu.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberPersonalityTypeRepository memberPersonalityTypeRepository;

    @Mock
    private MemberPersonalityRepository memberPersonalityRepository;

    @Test
    @DisplayName("회원 프로필 생성 성공 테스트")
    void createProfile_Success() {
        // given
        Long memberId = 1L;
        Member member = Member.builder()
                .email("test@example.com")
                .nickname("임시닉네임")
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);

        MemberCreateRequest request = new MemberCreateRequest();
        ReflectionTestUtils.setField(request, "nickname", "새닉네임");
        ReflectionTestUtils.setField(request, "age", 25);
        ReflectionTestUtils.setField(request, "gender", Gender.MALE);
        ReflectionTestUtils.setField(request, "personalityTypeIds", List.of(1L, 2L));

        MemberPersonalityType type1 = MemberPersonalityType.builder().id(1L).name("유형1").code("TYPE1").build();
        MemberPersonalityType type2 = MemberPersonalityType.builder().id(2L).name("유형2").code("TYPE2").build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.existsByNickname("새닉네임")).willReturn(false);
        given(memberPersonalityTypeRepository.findAllById(any())).willReturn(List.of(type1, type2));

        // when
        MemberResponse response = memberService.createProfile(memberId, request);

        // then
        assertThat(response.getNickname()).isEqualTo("새닉네임");
        assertThat(response.getAge()).isEqualTo(25);
        assertThat(response.getGender()).isEqualTo(Gender.MALE);
        assertThat(response.getPersonalityTypes()).hasSize(2);
        
        verify(memberPersonalityRepository, times(1)).deleteByMember(any());
        verify(memberPersonalityRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("중복된 닉네임으로 프로필 생성 시 예외 발생")
    void createProfile_DuplicateNickname() {
        // given
        Long memberId = 1L;
        Member member = Member.builder()
                .email("test@example.com")
                .nickname("임시닉네임")
                .build();
        
        MemberCreateRequest request = new MemberCreateRequest();
        ReflectionTestUtils.setField(request, "nickname", "중복닉네임");

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.existsByNickname("중복닉네임")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.createProfile(memberId, request))
                .isInstanceOf(MemberException.class);
    }
}
