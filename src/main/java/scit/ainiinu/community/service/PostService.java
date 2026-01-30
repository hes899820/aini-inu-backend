package scit.ainiinu.community.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scit.ainiinu.community.dto.PostCreateRequest;
import scit.ainiinu.community.dto.PostResponse;
import scit.ainiinu.community.entity.Post;
import scit.ainiinu.community.repository.PostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;

    //새글 작성
    public PostResponse create(Long authorId, PostCreateRequest request) {
        Post post = Post.create(
                authorId,
                request.getContent(),
                request.getImageUrls()
        );
        Post saved = postRepository.save(post);
        return PostResponse.from(saved);
    }
}
