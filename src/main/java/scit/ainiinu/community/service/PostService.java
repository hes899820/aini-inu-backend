package scit.ainiinu.community.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scit.ainiinu.community.dto.PostCreateRequest;
import scit.ainiinu.community.dto.PostResponse;

import java.util.List;

@Service
@RequiredArgsConstructor

public class PostService {
    public List<PostResponse> getPosts() {
        return List.of();//임시
    }

    @Transactional
    public PostResponse create(Long memberId, PostCreateRequest request) {
        return null; // 임시
    }
}
