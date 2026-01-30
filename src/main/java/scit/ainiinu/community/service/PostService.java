package scit.ainiinu.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scit.ainiinu.common.exception.BusinessException;
import scit.ainiinu.common.response.SliceResponse;
import scit.ainiinu.community.dto.CommentCreateRequest;
import scit.ainiinu.community.dto.CommentResponse;
import scit.ainiinu.community.dto.PostCreateRequest;
import scit.ainiinu.community.dto.PostDetailResponse;
import scit.ainiinu.community.dto.PostLikeResponse;
import scit.ainiinu.community.dto.PostResponse;
import scit.ainiinu.community.dto.PostUpdateRequest;
import scit.ainiinu.community.entity.Comment;
import scit.ainiinu.community.entity.Post;
import scit.ainiinu.community.entity.PostLike;
import scit.ainiinu.community.exception.CommunityErrorCode;
import scit.ainiinu.community.repository.CommentRepository;
import scit.ainiinu.community.repository.PostLikeRepository;
import scit.ainiinu.community.repository.PostRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    /**
     * 게시글 목록 조회 (무한 스크롤)
     * - 차단한 사용자의 게시글은 조회되지 않습니다.
     */
    public SliceResponse<PostResponse> getPosts(Pageable pageable) {
        // TODO: Member Context 완성 후 실제 차단 목록 조회 로직 추가
        List<Long> blockedUserIds = Collections.emptyList();

        Slice<Post> posts;
        if (blockedUserIds.isEmpty()) {
            posts = postRepository.findAllBy(pageable);
        } else {
            posts = postRepository.findByAuthorIdNotIn(blockedUserIds, pageable);
        }

        return SliceResponse.of(posts.map(PostResponse::from));
    }

    /**
     * 게시글 상세 조회 (댓글 포함)
     * - 게시글 내용과 해당 게시글에 달린 댓글 목록을 반환합니다.
     * - 차단한 사용자의 댓글은 조회 목록에서 제외됩니다.
     */
    public PostDetailResponse getPostDetail(Long postId) {
        // 1. 게시글 조회 (없으면 CO001 예외)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(CommunityErrorCode.POST_NOT_FOUND));

        // 2. 댓글 목록 조회
        List<Comment> comments = commentRepository.findAllByPostOrderByCreatedAtAsc(post);

        // 3. 차단된 사용자의 댓글 필터링
        // TODO: Member Context 완성 후 실제 차단 목록 조회
        List<Long> blockedUserIds = Collections.emptyList(); 
        
        List<CommentResponse> commentResponses = comments.stream()
                .filter(comment -> !blockedUserIds.contains(comment.getAuthorId()))
                .map(CommentResponse::from)
                .collect(Collectors.toList());

        return PostDetailResponse.of(post, commentResponses);
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public PostResponse create(Long authorId, PostCreateRequest request) {
        Post post = Post.create(
                authorId,
                request.getContent(),
                request.getImageUrls()
        );
        Post saved = postRepository.save(post);
        return PostResponse.from(saved);
    }

    /**
     * 게시글 수정
     * - 작성자 본인만 수정 가능합니다. (CO002)
     */
    @Transactional
    public PostResponse updatePost(Long authorId, Long postId, PostUpdateRequest request) {
        // 1. 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(CommunityErrorCode.POST_NOT_FOUND));

        // 2. 작성자 본인 확인
        if (!post.getAuthorId().equals(authorId)) {
            throw new BusinessException(CommunityErrorCode.NOT_POST_OWNER);
        }

        // 3. 내용 및 이미지 수정 (길이 검증 등은 Entity 내부에서 처리)
        // TODO: 2000자 초과 시 CO005 예외 처리 (현재는 INVALID_CONTENT 사용 중이나, 필요 시 세분화)
        post.update(request.getContent(), request.getImageUrls());

        return PostResponse.from(post);
    }

    /**
     * 게시글 삭제
     * - 작성자 본인만 삭제 가능합니다. (CO002)
     */
    @Transactional
    public void deletePost(Long authorId, Long postId) {
        // 1. 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(CommunityErrorCode.POST_NOT_FOUND));

        // 2. 작성자 본인 확인
        if (!post.getAuthorId().equals(authorId)) {
            throw new BusinessException(CommunityErrorCode.NOT_POST_OWNER);
        }

        // 3. 게시글 삭제
        postRepository.delete(post);
    }

    /**
     * 좋아요 토글 (생성/취소)
     * - 이미 좋아요를 누른 경우 -> 취소 (카운트 감소)
     * - 누르지 않은 경우 -> 생성 (카운트 증가)
     */
    @Transactional
    public PostLikeResponse toggleLike(Long memberId, Long postId) {
        // 1. 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(CommunityErrorCode.POST_NOT_FOUND));

        // 2. 좋아요 존재 여부 확인
        Optional<PostLike> existingLike = postLikeRepository.findByPostAndMemberId(post, memberId);

        boolean isLiked;
        
        if (existingLike.isPresent()) {
            // 3. 이미 존재하면 삭제 (좋아요 취소) 및 카운트 감소
            postLikeRepository.delete(existingLike.get());
            post.decreaseLike();
            isLiked = false;
        } else {
            // 4. 없으면 생성 (좋아요) 및 카운트 증가
            postLikeRepository.save(PostLike.create(post, memberId));
            post.increaseLike();
            isLiked = true;
        }

        return new PostLikeResponse(isLiked, post.getLikeCount());
    }

    /**
     * 댓글 작성
     * - 댓글 작성 시 게시글의 댓글 수가 1 증가합니다.
     * - 내용은 최대 500자입니다. (CO005)
     */
    @Transactional
    public CommentResponse createComment(Long authorId, Long postId, CommentCreateRequest request) {
        // 1. 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(CommunityErrorCode.POST_NOT_FOUND));

        // 2. 내용 길이 검증 (Entity 저장 전 DTO validation 또는 여기서 수행)
        if (request.getContent().length() > 500) {
            throw new BusinessException(CommunityErrorCode.INVALID_CONTENT_LENGTH);
        }

        // 3. 댓글 엔티티 생성 및 저장
        Comment comment = Comment.create(post, authorId, request.getContent());
        Comment savedComment = commentRepository.save(comment);

        // 4. 게시글 댓글 수 증가
        post.increaseComment();

        return CommentResponse.from(savedComment);
    }

    /**
     * 댓글 삭제
     * - 댓글 작성자 본인만 삭제 가능합니다. (CO004)
     * - 삭제 시 게시글의 댓글 수가 1 감소합니다.
     */
    @Transactional
    public void deleteComment(Long authorId, Long postId, Long commentId) {
        // 1. 게시글 조회 (댓글 수 감소를 위해 필요)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(CommunityErrorCode.POST_NOT_FOUND));

        // 2. 댓글 조회 (없으면 CO003)
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(CommunityErrorCode.COMMENT_NOT_FOUND));

        // 3. 작성자 본인 확인 (아니면 CO004)
        if (!comment.getAuthorId().equals(authorId)) {
            throw new BusinessException(CommunityErrorCode.NOT_COMMENT_OWNER);
        }

        // 4. 댓글 삭제 및 카운트 감소
        commentRepository.delete(comment);
        post.decreaseComment();
    }
}
