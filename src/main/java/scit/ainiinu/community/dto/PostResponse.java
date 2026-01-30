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

    /**
     * 좋아요 여부를 포함하여 PostResponse 생성
     * @param post 게시글 엔티티
     * @param memberId 현재 로그인 사용자 ID
     * @param postLikeRepository 좋아요 여부 확인을 위한 Repository
     * @return PostResponse
     */
    public static PostResponse from(Post post, Long memberId,
                                     scit.ainiinu.community.repository.PostLikeRepository postLikeRepository) {
        PostResponse r = new PostResponse();
        r.id = post.getId();
        r.content = post.getContent();
        r.imageUrls = post.getImageUrls();
        r.likeCount = post.getLikeCount();
        r.commentCount = post.getCommentCount();
        // 현재 사용자의 좋아요 여부 확인
        r.isLiked = postLikeRepository.existsByPostAndMemberId(post, memberId);
        r.createdAt = post.getCreatedAt();
        // TODO: Member 연동 후 실제 Author 정보 채움
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
