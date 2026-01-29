package scit.ainiinu.community.dto;

import lombok.Data;
import lombok.Getter;
import scit.ainiinu.community.entity.Post;

import java.time.LocalDateTime;

@Data
public class PostResponse {
    private Long id;
    private Long authorId;
    private String content;
    private String imageUrls;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;

    public static PostResponse from(Post p) {
        PostResponse r = new PostResponse();
        r.id = p.getId();
        r.authorId = p.getAuthorId();
        r.content = p.getContent();
        r.imageUrls = p.getImageUrls();
        r.likeCount = p.getLikeCount();
        r.commentCount = p.getCommentCount();
        r.createdAt = p.getCreatedAt();
        return r;
    }
}
