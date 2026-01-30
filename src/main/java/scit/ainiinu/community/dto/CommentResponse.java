package scit.ainiinu.community.dto;

import lombok.Data;
import lombok.Getter;
import scit.ainiinu.community.entity.Comment;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id;
    private Author author;
    private String content;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        CommentResponse r = new CommentResponse();
        r.id = comment.getId();
        r.content = comment.getContent();
        r.createdAt = comment.getCreatedAt();
        r.author = Author.of(comment.getAuthorId());
        return r;
    }

    @Getter
    public static class Author {
        private Long id;
        private String nickname;
        private String profileImageUrl;

        public static Author of(Long memberId) {
            Author a = new Author();
            a.id = memberId;
            return a;
        }
    }
}
