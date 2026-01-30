package scit.ainiinu.community.entity;

import org.junit.jupiter.api.Test;
import scit.ainiinu.common.exception.BusinessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void create_success() {
        Post post = Post.create(1L, "hello", List.of("a.jpg", "b.jpg"));

        assertEquals(1L, post.getAuthorId());
        assertEquals("hello", post.getContent());
        assertEquals(2, post.getImageUrls().size());
        assertEquals(0, post.getLikeCount());
        assertEquals(0, post.getCommentCount());
    }

    @Test
    void create_fail_when_content_blank() {
        assertThrows(BusinessException.class, () ->
                Post.create(1L, "   ", List.of())
        );
    }

    @Test
    void create_fail_when_images_over_5() {
        List<String> images = List.of("1","2","3","4","5","6");

        assertThrows(BusinessException.class, () ->
                Post.create(1L, "ok", images)
        );
    }

    @Test
    void like_never_below_zero() {
        Post post = Post.create(1L, "ok", List.of());

        post.decreaseLike();
        assertEquals(0, post.getLikeCount());

        post.increaseLike();
        post.decreaseLike();
        post.decreaseLike();
        assertEquals(0, post.getLikeCount());
    }

    @Test
    void comment_never_below_zero() {
        Post post = Post.create(1L, "ok", List.of());

        post.increaseComment();
        post.increaseComment();
        post.decreaseComment();
        post.decreaseComment();
        post.decreaseComment();

        assertEquals(0, post.getCommentCount());
    }
}

