package scit.ainiinu.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scit.ainiinu.common.response.ApiResponse;
import scit.ainiinu.common.security.annotation.Public;
import scit.ainiinu.member.dto.request.LoginRequest;
import scit.ainiinu.member.dto.request.TokenRefreshRequest;
import scit.ainiinu.member.dto.response.LoginResponse;
import scit.ainiinu.member.entity.enums.SocialProvider;
import scit.ainiinu.member.service.AuthService;

/**
 * 인증 관련 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 소셜 로그인 API
     *
     * @param provider 소셜 공급자 (kakao, naver, google)
     * @param request  소셜 액세스 토큰
     * @return 자체 JWT 토큰 정보
     */
    @Public
    @PostMapping("/login/{provider}")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @PathVariable("provider") String provider,
            @Valid @RequestBody LoginRequest request
    ) {
        SocialProvider socialProvider = SocialProvider.valueOf(provider.toUpperCase());
        LoginResponse response = authService.login(socialProvider, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 토큰 갱신 API
     * Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.
     */
    @Public
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @Valid @RequestBody TokenRefreshRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(request)));
    }
}
