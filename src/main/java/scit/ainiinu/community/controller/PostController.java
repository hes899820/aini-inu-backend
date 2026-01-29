package scit.ainiinu.community.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import scit.ainiinu.common.response.ApiResponse;
import scit.ainiinu.community.dto.PostCreateRequest;
import scit.ainiinu.community.dto.PostResponse;
import scit.ainiinu.community.service.PostService;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    
    // TODO: PostService를 주입받고 API 엔드포인트를 구현해보세요.
    // 참고: ResponseEntity<ApiResponse<T>> 형식을 사용해야 합니다.

    private final PostService postService; //PostService 주입

    //게시글 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success(postService.getPosts()));
    }

    //게시글 생성
    // Security 적용 후 @AuthenticationPrincipal로 memberId 주입 예정

//    @PostMapping
//    public ResponseEntity<ApiResponse<PostResponse>> create(
//           // @AuthenticationPrincipal CustomUserDetails user,
//
//            @RequestBody @Valid PostCreateRequest request
//    ) {
//        PostResponse response = postService.create(user.getAuthorId(),request);
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }
}
