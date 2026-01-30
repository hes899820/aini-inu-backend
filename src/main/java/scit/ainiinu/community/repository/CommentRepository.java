package scit.ainiinu.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scit.ainiinu.community.entity.Comment;
import scit.ainiinu.community.entity.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 게시글에 달린 댓글 목록 조회 (작성일 오름차순)
    List<Comment> findAllByPostOrderByCreatedAtAsc(Post post);
}