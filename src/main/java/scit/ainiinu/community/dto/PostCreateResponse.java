package scit.ainiinu.community.dto;

import lombok.Data;
import scit.ainiinu.community.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

/**
 * POST /posts 응답 DTO (게시글 생성 응답)
 * - API 명세에 따라 isLiked 필드 제외
 */
@Data
public class PostCreateResponse {
    private Long id;
    private PostResponse.Author author;
    private String content;
    private List<String> imageUrls;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;

    public static PostCreateResponse from(Post post) {
        PostCreateResponse r = new PostCreateResponse();
        r.id = post.getId();
        r.content = post.getContent();
        r.imageUrls = post.getImageUrls();
        r.likeCount = post.getLikeCount();
        r.commentCount = post.getCommentCount();
        r.createdAt = post.getCreatedAt();
        // TODO: Member 연동 후 실제 Author 정보 채움
        r.author = PostResponse.Author.of(post.getAuthorId());
        return r;
    }
}
