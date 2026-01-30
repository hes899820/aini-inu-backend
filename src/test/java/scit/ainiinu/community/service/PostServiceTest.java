package scit.ainiinu.community.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import scit.ainiinu.common.config.JpaConfig;
import scit.ainiinu.community.dto.PostCreateRequest;
import scit.ainiinu.community.entity.Post;
import scit.ainiinu.community.repository.PostRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void create_post_success() {
        // given
        PostCreateRequest req = new PostCreateRequest();
        req.setContent("테스트 글");
        req.setImageUrls(List.of("a.jpg"));

        PostService service = new PostService(postRepository);

        // when
        var res = service.create(1L, req);

        // then
        assertThat(res.getId()).isNotNull();
        assertThat(res.getContent()).isEqualTo("테스트 글");
        assertThat(res.getImageUrls().size()).isEqualTo(1);
        assertThat(res.getLikeCount()).isZero();
    }
}

