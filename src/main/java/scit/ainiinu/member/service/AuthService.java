package scit.ainiinu.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scit.ainiinu.common.security.jwt.JwtTokenProvider;
import scit.ainiinu.member.dto.request.LoginRequest;
import scit.ainiinu.member.dto.response.LoginResponse;
import scit.ainiinu.member.entity.Member;
import scit.ainiinu.member.entity.RefreshToken;
import scit.ainiinu.member.entity.enums.MemberStatus;
import scit.ainiinu.member.entity.enums.SocialProvider;
import scit.ainiinu.member.exception.MemberErrorCode;
import scit.ainiinu.member.exception.MemberException;
import scit.ainiinu.member.repository.MemberRepository;
import scit.ainiinu.member.repository.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 인증 관련 비즈니스 로직 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 소셜 로그인 처리
     *
     * @param provider 소셜 공급자 (KAKAO, NAVER, GOOGLE)
     * @param request  로그인 요청 (소셜 액세스 토큰 포함)
     * @return 로그인 응답 (자체 JWT 토큰 포함)
     */
    @Transactional
    public LoginResponse login(SocialProvider provider, LoginRequest request) {
        // 1. 소셜 액세스 토큰을 통해 사용자 정보 가져오기 (현재는 시뮬레이션)
        // TODO: 실제 소셜 서버 API 호출 로직 구현 필요
        String socialEmail = simulateSocialLogin(provider, request.getAccessToken());
        String socialId = "SOCIAL_" + UUID.randomUUID().toString().substring(0, 8);

        // 2. 기존 회원 여부 확인
        return memberRepository.findByEmail(socialEmail)
                .map(member -> handleExistingMember(member))
                .orElseGet(() -> handleNewMember(socialEmail, socialId, provider));
    }

    /**
     * 기존 회원 로그인 처리
     */
    private LoginResponse handleExistingMember(Member member) {
        if (member.getStatus() == MemberStatus.BANNED) {
            throw new MemberException(MemberErrorCode.BANNED_MEMBER);
        }

        return createLoginResponse(member, false);
    }

    /**
     * 신규 회원 가입 및 로그인 처리
     */
    private LoginResponse handleNewMember(String email, String socialId, SocialProvider provider) {
        // 임시 닉네임 생성 (중복 방지를 위해 UUID 활용)
        String tempNickname = "USER_" + UUID.randomUUID().toString().substring(0, 5);

        Member newMember = Member.builder()
                .email(email)
                .nickname(tempNickname)
                .socialProvider(provider)
                .socialId(socialId)
                .build();

        memberRepository.save(newMember);
        return createLoginResponse(newMember, true);
    }

    /**
     * 자체 JWT 토큰 생성 및 응답 객체 조립
     */
    private LoginResponse createLoginResponse(Member member, boolean isNewMember) {
        String accessToken = jwtTokenProvider.generateAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());

        // Refresh Token 저장 (RTR 패턴 적용 가능)
        saveRefreshToken(member, refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L) // 1시간
                .isNewMember(isNewMember)
                .memberId(member.getId())
                .build();
    }

    private void saveRefreshToken(Member member, String tokenValue) {
        // 기존 토큰 삭제 후 저장 (1인 1세션 정책 또는 단순 덮어쓰기)
        refreshTokenRepository.deleteByMember(member);

        RefreshToken refreshToken = RefreshToken.builder()
                .member(member)
                .tokenHash(tokenValue)
                .expiresAt(LocalDateTime.now().plusDays(14))
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * 소셜 로그인 시뮬레이션 (실제 연동 전 테스트용)
     */
    private String simulateSocialLogin(SocialProvider provider, String accessToken) {
        if (accessToken.equals("invalid-token")) {
            throw new MemberException(MemberErrorCode.INVALID_SOCIAL_TOKEN);
        }
        // 토큰값에 따라 이메일을 결정하는 가짜 로직
        return provider.name().toLowerCase() + "_" + accessToken.hashCode() + "@example.com";
    }
}
