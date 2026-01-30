package scit.ainiinu.community.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scit.ainiinu.common.response.ApiResponse;
import scit.ainiinu.common.response.SliceResponse;
import scit.ainiinu.community.dto.CommentCreateRequest;
import scit.ainiinu.community.dto.CommentResponse;
import scit.ainiinu.community.dto.PostCreateRequest;
import scit.ainiinu.community.dto.PostCreateResponse;
import scit.ainiinu.community.dto.PostDetailResponse;
import scit.ainiinu.community.dto.PostLikeResponse;
import scit.ainiinu.community.dto.PostResponse;
import scit.ainiinu.community.dto.PostUpdateRequest;
import scit.ainiinu.community.service.PostService;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 목록 조회 (무한 스크롤)
    @GetMapping
    public ResponseEntity<ApiResponse<SliceResponse<PostResponse>>> getPosts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // TODO Security 적용 후: 로그인 사용자에서 memberId 추출
        Long memberId = 1L; // 임시 사용자
        SliceResponse<PostResponse> response = postService.getPosts(memberId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(
            @PathVariable("postId") Long postId
    ) {
        // TODO Security 적용 후: 로그인 사용자에서 memberId 추출
        Long memberId = 1L; // 임시 사용자
        PostDetailResponse response = postService.getPostDetail(memberId, postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody @Valid PostUpdateRequest request
    ) {
        // TODO Security 적용 후: 로그인 사용자에서 authorId 추출
        Long authorId = 1L; //임시 사용자
        PostResponse response = postService.updatePost(authorId, postId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable("postId") Long postId
    ) {
        // TODO Security 적용 후: 로그인 사용자에서 authorId 추출
        Long authorId = 1L; // 임시 사용자
        postService.deletePost(authorId, postId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 좋아요 토글
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<PostLikeResponse>> toggleLike(
            @PathVariable("postId") Long postId
    ) {
        // TODO Security 적용 후: 로그인 사용자에서 authorId 추출
        Long memberId = 1L; // 임시 사용자
        PostLikeResponse response = postService.toggleLike(memberId, postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 댓글 작성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable("postId") Long postId,
            @RequestBody @Valid CommentCreateRequest request
    ) {
        // TODO Security 적용 후: 로그인 사용자에서 authorId 추출
        Long authorId = 1L; // 임시 사용자
        CommentResponse response = postService.createComment(authorId, postId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 댓글 삭제
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId
    ) {
        // TODO Security 적용 후: 로그인 사용자에서 authorId 추출
        Long authorId = 1L; // 임시 사용자
        postService.deleteComment(authorId, postId, commentId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    //게시글 생성
    @PostMapping
    public ResponseEntity<ApiResponse<PostCreateResponse>> create(
            @RequestBody @Valid PostCreateRequest request
    ){
        // TODO Security 적용 후: 로그인 사용자에서 authorId 추출
        Long authorId = 1L; //임시 사용자
        PostCreateResponse response = postService.create(authorId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}