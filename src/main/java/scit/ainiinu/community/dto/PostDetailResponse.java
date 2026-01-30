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

    public static PostDetailResponse of(Post post, List<CommentResponse> comments) {
        PostDetailResponse r = new PostDetailResponse();
        r.id = post.getId();
        r.content = post.getContent();
        r.imageUrls = post.getImageUrls();
        r.likeCount = post.getLikeCount();
        r.commentCount = post.getCommentCount();
        r.isLiked = false; // TODO: 좋아요 연동
        r.createdAt = post.getCreatedAt();
        r.author = PostResponse.Author.of(post.getAuthorId());
        r.comments = comments;
        return r;
    }
}
