package scit.ainiinu.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scit.ainiinu.community.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    // TODO: 필요한 쿼리 메서드를 정의해보세요. (예: findAllByAuthorId)
}
