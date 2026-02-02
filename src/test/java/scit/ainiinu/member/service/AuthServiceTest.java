package scit.ainiinu.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import scit.ainiinu.common.security.jwt.JwtTokenProvider;
import scit.ainiinu.member.dto.request.LoginRequest;
import scit.ainiinu.member.dto.response.LoginResponse;
import scit.ainiinu.member.entity.Member;
import scit.ainiinu.member.entity.enums.SocialProvider;
import scit.ainiinu.member.repository.MemberRepository;
import scit.ainiinu.member.repository.RefreshTokenRepository;

import java.util.Optional;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("기존 회원이 소셜 로그인을 하면 isNewMember는 false를 반환한다.")
    void login_ExistingMember() {
        // given
        String email = "kakao_12345@example.com";
        SocialProvider provider = SocialProvider.KAKAO;
        LoginRequest request = new LoginRequest("social-access-token");
        
        Member existingMember = Member.builder()
                .email(email)
                .nickname("기존유저")
                .socialProvider(provider)
                .build();
        ReflectionTestUtils.setField(existingMember, "id", 1L);
        
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(existingMember));
        given(jwtTokenProvider.generateAccessToken(any())).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken(any())).willReturn("refresh-token");

        // when
        LoginResponse response = authService.login(provider, request);

        // then
        assertThat(response.isNewMember()).isFalse();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getMemberId()).isEqualTo(1L);
        verify(refreshTokenRepository, times(1)).deleteByMember(any());
        verify(refreshTokenRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("신규 회원이 소셜 로그인을 하면 회원을 저장하고 isNewMember는 true를 반환한다.")
    void login_NewMember() {
        // given
        SocialProvider provider = SocialProvider.GOOGLE;
        LoginRequest request = new LoginRequest("social-access-token");
        
        given(memberRepository.findByEmail(any())).willReturn(Optional.empty());
        given(memberRepository.save(any())).willAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            ReflectionTestUtils.setField(member, "id", 2L);
            return member;
        });
        given(jwtTokenProvider.generateAccessToken(any())).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken(any())).willReturn("refresh-token");

        // when
        LoginResponse response = authService.login(provider, request);

        // then
        assertThat(response.isNewMember()).isTrue();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getMemberId()).isEqualTo(2L);
        verify(memberRepository, times(1)).save(any());
        verify(refreshTokenRepository, times(1)).save(any());
    }
}
