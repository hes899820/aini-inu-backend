package scit.ainiinu.community.dto;

import lombok.Data;
import lombok.Getter;
import scit.ainiinu.community.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponse {
    private Long id;
    private Author author; //작성자 정보 묶음
    private String content;
    private List<String> imageUrls;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;
    private LocalDateTime createdAt;

    public static PostResponse from(Post post) {
        PostResponse r = new PostResponse();
        r.id = post.getId();
        r.content = post.getContent();
        r.imageUrls = post.getImageUrls();
        r.likeCount = post.getLikeCount();
        r.commentCount = post.getCommentCount();
        r.isLiked = false; // TODO: 좋아요 여부 로직 연동 필요
        r.createdAt = post.getCreatedAt();
        //Member 아직 없으므로 임시
        r.author = Author.of(post.getAuthorId());
        return r;
    }

    //작성자정보
    @Getter
    public static class Author{
        private Long id;
        private String nickname;
        private String profileImageUrl;

        public static Author of(Long memberId){
            Author a = new Author();
            a.id = memberId;
            // TODO Member 연동 후 채움
            a.nickname = null;
            a.profileImageUrl = null;
            return a;
        }
    }
}
