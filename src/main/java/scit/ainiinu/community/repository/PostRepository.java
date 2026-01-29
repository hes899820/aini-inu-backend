package scit.ainiinu.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scit.ainiinu.community.entity.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // TODO: 필요한 쿼리 메서드를 정의해보세요. (예: findAllByAuthorId)

    //최신 글
    List<Post> findAllByOrderByCreatedAtDesc();
    //특정 아이디의 게시글
    List<Post> findAllByAuthorId(Long authorId);
    //좋아요 많은 순
    List<Post> findAllByOrderByLikeCountDesc();
    //댓글 많은 순
    List<Post> findAllByOrderByCommentCountDesc();

}
