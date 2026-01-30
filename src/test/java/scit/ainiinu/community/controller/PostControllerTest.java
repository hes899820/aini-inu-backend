package scit.ainiinu.community.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import scit.ainiinu.community.dto.CommentResponse;
import scit.ainiinu.community.dto.PostCreateRequest;
import scit.ainiinu.community.dto.PostDetailResponse;
import scit.ainiinu.community.dto.PostResponse;
import scit.ainiinu.community.service.PostService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("게시글 생성")
    class CreatePost {
        @Test
        @WithMockUser
        @DisplayName("유효한 요청으로 게시글을 생성하면 성공한다")
        void create_post_api_success() throws Exception {
            // given
            PostResponse dummy = new PostResponse();
            given(postService.create(anyLong(), any())).willReturn(dummy);

            String body = """
                    {
                      "content": "오늘 산책 최고!",
                      "imageUrls": ["a.jpg", "b.jpg"]
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/v1/posts")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("게시글 상세 조회")
    class GetPostDetail {
        @Test
        @WithMockUser
        @DisplayName("게시글 ID로 조회하면 상세 정보와 댓글 목록을 반환한다")
        void get_post_detail_success() throws Exception {
            // given
            Long postId = 1L;
            PostDetailResponse dummyResponse = new PostDetailResponse();
            dummyResponse.setId(postId);
            dummyResponse.setContent("상세 내용");
            
            CommentResponse commentDummy = new CommentResponse();
            commentDummy.setId(10L);
            commentDummy.setContent("댓글 내용");
            dummyResponse.setComments(List.of(commentDummy));

            given(postService.getPostDetail(postId)).willReturn(dummyResponse);

            // when & then
            mockMvc.perform(get("/api/v1/posts/{postId}", postId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(postId))
                    .andExpect(jsonPath("$.data.comments[0].content").value("댓글 내용"));
        }
    }
}