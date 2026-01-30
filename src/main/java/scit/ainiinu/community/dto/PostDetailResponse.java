package scit.ainiinu.community.dto;

import lombok.Data;
import scit.ainiinu.community.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDetailResponse {
    private Long id;
    private PostResponse.Author author;
    private String content;
    private List<String> imageUrls;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;
    private LocalDateTime createdAt;
    private List<CommentResponse> comments;

    /**
     * 좋아요 여부를 포함하여 PostDetailResponse 생성
     * @param post 게시글 엔티티
     * @param comments 댓글 목록
     * @param memberId 현재 로그인 사용자 ID
     * @param postLikeRepository 좋아요 여부 확인을 위한 Repository
     * @return PostDetailResponse
     */
    public static PostDetailResponse of(Post post, List<CommentResponse> comments, Long memberId,
                                        scit.ainiinu.community.repository.PostLikeRepository postLikeRepository) {
        PostDetailResponse r = new PostDetailResponse();
        r.id = post.getId();
        r.content = post.getContent();
        r.imageUrls = post.getImageUrls();
        r.likeCount = post.getLikeCount();
        r.commentCount = post.getCommentCount();
        // 현재 사용자의 좋아요 여부 확인
        r.isLiked = postLikeRepository.existsByPostAndMemberId(post, memberId);
        r.createdAt = post.getCreatedAt();
        r.author = PostResponse.Author.of(post.getAuthorId());
        r.comments = comments;
        return r;
    }
}
