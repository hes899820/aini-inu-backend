package scit.ainiinu.community.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import scit.ainiinu.common.exception.BusinessException;
import scit.ainiinu.common.response.SliceResponse;
import scit.ainiinu.community.dto.CommentCreateRequest;
import scit.ainiinu.community.dto.CommentResponse;
import scit.ainiinu.community.dto.PostCreateRequest;
import scit.ainiinu.community.dto.PostDetailResponse;
import scit.ainiinu.community.dto.PostLikeResponse;
import scit.ainiinu.community.dto.PostResponse;
import scit.ainiinu.community.dto.PostUpdateRequest;
import scit.ainiinu.community.entity.Comment;
import scit.ainiinu.community.entity.Post;
import scit.ainiinu.community.entity.PostLike;
import scit.ainiinu.community.exception.CommunityErrorCode;
import scit.ainiinu.community.repository.CommentRepository;
import scit.ainiinu.community.repository.PostLikeRepository;
import scit.ainiinu.community.repository.PostRepository;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private PostService postService;

    @Nested
    @DisplayName("댓글 작성")
    class CreateComment {

        @Test
        @DisplayName("유효한 요청으로 댓글을 작성하면 성공하고 게시글의 댓글 수가 증가한다")
        void success() {
            // given
            Long postId = 1L;
            Long authorId = 1L;
            Post post = Post.create(authorId, "Content", Collections.emptyList());
            setId(post, postId);

            CommentCreateRequest request = new CommentCreateRequest();
            request.setContent("Nice dog!");

            Comment savedComment = Comment.create(postId, authorId, "Nice dog!");
            setId(savedComment, 10L);

            given(postRepository.findById(postId)).willReturn(Optional.of(post));
            given(commentRepository.save(any(Comment.class))).willReturn(savedComment);

            // when
            CommentResponse response = postService.createComment(authorId, postId, request);

            // then
            assertThat(response.getContent()).isEqualTo("Nice dog!");
            assertThat(post.getCommentCount()).isEqualTo(1); // 댓글 수 증가 확인
            then(commentRepository).should(times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("존재하지 않는 게시글에 댓글을 작성하려 하면 예외가 발생한다")
        void fail_PostNotFound() {
            // given
            Long postId = 999L;
            given(postRepository.findById(postId)).willReturn(Optional.empty());

            CommentCreateRequest request = new CommentCreateRequest();
            request.setContent("Test");

            // when & then
            assertThatThrownBy(() -> postService.createComment(1L, postId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CommunityErrorCode.POST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class DeleteComment {

        @Test
        @DisplayName("작성자가 본인의 댓글을 삭제하면 성공하고 댓글 수가 감소한다")
        void success() {
            // given
            Long postId = 1L;
            Long commentId = 10L;
            Long authorId = 1L;

            Post post = Post.create(authorId, "Content", Collections.emptyList());
            setId(post, postId);
            post.increaseComment(); // 댓글 1개 있는 상태

            Comment comment = Comment.create(postId, authorId, "Content");
            setId(comment, commentId);

            given(postRepository.findById(postId)).willReturn(Optional.of(post));
            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

            // when
            postService.deleteComment(authorId, postId, commentId);

            // then
            assertThat(post.getCommentCount()).isEqualTo(0); // 댓글 수 감소 확인
            then(commentRepository).should(times(1)).delete(comment);
        }

        @Test
        @DisplayName("작성자가 아닌 사용자가 댓글을 삭제하려 하면 예외가 발생한다")
        void fail_NotOwner() {
            // given
            Long postId = 1L;
            Long commentId = 10L;
            Long authorId = 1L;
            Long otherUserId = 2L;

            Post post = Post.create(authorId, "Content", Collections.emptyList());
            Comment comment = Comment.create(postId, authorId, "Content");

            given(postRepository.findById(postId)).willReturn(Optional.of(post));
            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

            // when & then
            assertThatThrownBy(() -> postService.deleteComment(otherUserId, postId, commentId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CommunityErrorCode.NOT_POST_OWNER);
        }
    }

    @Nested
    @DisplayName("좋아요 토글")
    class ToggleLike {

        @Test
        @DisplayName("좋아요가 없을 때 요청하면 생성되고 카운트가 증가한다")
        void createLike() {
            // given
            Long postId = 1L;
            Long memberId = 1L;
            Post post = Post.create(memberId, "Content", Collections.emptyList());
            setId(post, postId);

            given(postRepository.findById(postId)).willReturn(Optional.of(post));
            given(postLikeRepository.findByPostAndMemberId(post, memberId)).willReturn(Optional.empty());

            // when
            PostLikeResponse response = postService.toggleLike(memberId, postId);

            // then
            assertThat(response.isLiked()).isTrue();
            assertThat(response.getLikeCount()).isEqualTo(1);
            then(postLikeRepository).should(times(1)).save(any(PostLike.class));
        }

        @Test
        @DisplayName("좋아요가 이미 있을 때 요청하면 삭제되고 카운트가 감소한다")
        void removeLike() {
            // given
            Long postId = 1L;
            Long memberId = 1L;
            Post post = Post.create(memberId, "Content", Collections.emptyList());
            setId(post, postId);
            post.increaseLike(); // 기존 좋아요 상태 반영 (count=1)

            PostLike existingLike = PostLike.create(post, memberId);
            given(postRepository.findById(postId)).willReturn(Optional.of(post));
            given(postLikeRepository.findByPostAndMemberId(post, memberId)).willReturn(Optional.of(existingLike));

            // when
            PostLikeResponse response = postService.toggleLike(memberId, postId);

            // then
            assertThat(response.isLiked()).isFalse();
            assertThat(response.getLikeCount()).isEqualTo(0);
            then(postLikeRepository).should(times(1)).delete(existingLike);
        }
    }

    @Nested
    @DisplayName("게시글 수정")
    class UpdatePost {

        @Test
        @DisplayName("작성자가 본인의 게시글을 수정하면 성공한다")
        void success() {
            // given
            Long postId = 1L;
            Long authorId = 1L;
            
            Post post = Post.create(authorId, "Original Content", Collections.emptyList());
            setId(post, postId);
            
            given(postRepository.findById(postId)).willReturn(Optional.of(post));
            
            PostUpdateRequest request = new PostUpdateRequest();
            request.setContent("Updated Content");
            request.setImageUrls(List.of("new.jpg"));

            // when
            PostResponse response = postService.updatePost(authorId, postId, request);

            // then
            assertThat(response.getContent()).isEqualTo("Updated Content");
            assertThat(response.getImageUrls()).contains("new.jpg");
        }

        @Test
        @DisplayName("작성자가 아닌 사용자가 수정하려 하면 예외가 발생한다")
        void fail_NotOwner() {
            // given
            Long postId = 1L;
            Long authorId = 1L; // 실제 작성자
            Long otherUserId = 2L; // 요청자 (다른 사람)
            
            Post post = Post.create(authorId, "Content", Collections.emptyList());
            given(postRepository.findById(postId)).willReturn(Optional.of(post));

            PostUpdateRequest request = new PostUpdateRequest();

            // when & then
            assertThatThrownBy(() -> postService.updatePost(otherUserId, postId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CommunityErrorCode.NOT_POST_OWNER);
        }

        @Test
        @DisplayName("존재하지 않는 게시글을 수정하려 하면 예외가 발생한다")
        void fail_NotFound() {
            // given
            Long postId = 999L;
            given(postRepository.findById(postId)).willReturn(Optional.empty());
            PostUpdateRequest request = new PostUpdateRequest();

            // when & then
            assertThatThrownBy(() -> postService.updatePost(1L, postId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CommunityErrorCode.POST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("게시글 삭제")
    class DeletePost {

        @Test
        @DisplayName("작성자가 본인의 게시글을 삭제하면 성공한다")
        void success() {
            // given
            Long postId = 1L;
            Long authorId = 1L;
            Post post = Post.create(authorId, "Content", Collections.emptyList());
            setId(post, postId);

            given(postRepository.findById(postId)).willReturn(Optional.of(post));

            // when
            postService.deletePost(authorId, postId);

            // then
            then(postRepository).should(times(1)).delete(post);
        }

        @Test
        @DisplayName("작성자가 아닌 사용자가 삭제하려 하면 예외가 발생한다")
        void fail_NotOwner() {
            // given
            Long postId = 1L;
            Long authorId = 1L;
            Long otherUserId = 2L;
            
            Post post = Post.create(authorId, "Content", Collections.emptyList());
            given(postRepository.findById(postId)).willReturn(Optional.of(post));

            // when & then
            assertThatThrownBy(() -> postService.deletePost(otherUserId, postId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CommunityErrorCode.NOT_POST_OWNER);
            
            then(postRepository).should(times(0)).delete(any());
        }
    }

    @Nested
    @DisplayName("게시글 상세 조회 (댓글 포함)")
    class GetPostDetail {

        @Test
        @DisplayName("게시글 ID로 상세 정보를 조회하면 댓글 목록과 함께 반환한다")
        void success() {
            // given
            Long postId = 100L;
            Post post = Post.create(1L, "Post Content", Collections.emptyList());
            setId(post, postId);

            Comment comment1 = Comment.create(postId, 2L, "Comment 1");
            setId(comment1, 1L);
            Comment comment2 = Comment.create(postId, 3L, "Comment 2");
            setId(comment2, 2L);

            given(postRepository.findById(postId)).willReturn(Optional.of(post));
            given(commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId))
                    .willReturn(List.of(comment1, comment2));

            // when
            PostDetailResponse response = postService.getPostDetail(postId);

            // then
            assertThat(response.getId()).isEqualTo(postId);
            assertThat(response.getContent()).isEqualTo("Post Content");
            assertThat(response.getComments()).hasSize(2);
            assertThat(response.getComments().get(0).getContent()).isEqualTo("Comment 1");
            assertThat(response.getComments().get(1).getContent()).isEqualTo("Comment 2");

            then(postRepository).should(times(1)).findById(postId);
            then(commentRepository).should(times(1)).findAllByPostIdOrderByCreatedAtAsc(postId);
        }

        @Test
        @DisplayName("존재하지 않는 게시글 ID로 조회하면 예외가 발생한다")
        void fail_NotFound() {
            // given
            Long postId = 999L;
            given(postRepository.findById(postId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postService.getPostDetail(postId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CommunityErrorCode.POST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("게시글 목록 조회 (무한 스크롤)")
    class GetPosts {

        @Test
        @DisplayName("차단 목록이 없을 때 전체 게시글을 조회한다")
        void success_WithoutBlockedUsers() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            
            // 테스트용 게시글 생성
            Post post1 = Post.create(1L, "Content 1", Collections.emptyList());
            setId(post1, 100L);
            
            Post post2 = Post.create(2L, "Content 2", Collections.emptyList());
            setId(post2, 101L);

            Slice<Post> postSlice = new SliceImpl<>(List.of(post1, post2), pageable, true);
            
            // Mocking: 차단 목록이 비어있으면 findAllBy 호출
            given(postRepository.findAllBy(pageable)).willReturn(postSlice);

            // when
            SliceResponse<PostResponse> response = postService.getPosts(pageable);

            // then
            assertThat(response.getContent()).hasSize(2);
            assertThat(response.getContent().get(0).getId()).isEqualTo(100L);
            assertThat(response.getContent().get(1).getId()).isEqualTo(101L);
            
            // 검증: findAllBy가 호출되어야 함
            then(postRepository).should(times(1)).findAllBy(pageable);
            // 검증: findByAuthorIdNotIn은 호출되지 않아야 함 (현재 로직상)
            then(postRepository).should(times(0)).findByAuthorIdNotIn(any(), any());
        }
    }

    // ID 설정을 위한 리플렉션 헬퍼 메서드
    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Test setup failed: cannot set ID", e);
        }
    }
}
