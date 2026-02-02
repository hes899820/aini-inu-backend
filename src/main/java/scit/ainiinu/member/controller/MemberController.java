package scit.ainiinu.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scit.ainiinu.common.response.ApiResponse;
import scit.ainiinu.common.security.annotation.CurrentMember;
import scit.ainiinu.member.dto.request.MemberCreateRequest;
import scit.ainiinu.member.dto.response.MemberResponse;
import scit.ainiinu.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 완료 (프로필 생성)
     * 소셜 로그인 후 추가 정보를 입력받아 가입을 완료합니다.
     */
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<MemberResponse>> createProfile(
            @CurrentMember Long memberId,
            @Valid @RequestBody MemberCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(memberService.createProfile(memberId, request)));
    }
}
