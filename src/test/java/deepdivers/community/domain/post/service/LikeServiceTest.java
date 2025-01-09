package deepdivers.community.domain.post.service;

import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.exception.LikeExceptionType;
import deepdivers.community.domain.post.entity.like.Like;
import deepdivers.community.domain.post.entity.like.LikeId;
import deepdivers.community.domain.post.repository.jpa.CommentRepository;
import deepdivers.community.domain.post.repository.jpa.LikeRepository;
import deepdivers.community.domain.post.repository.jpa.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @MockBean
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    private final Long memberId = 1L;
    private final Long postId = 1L;
    private final Long commentId = 2L;

    @BeforeEach
    void setUp() {
        likeService = new LikeService(likeRepository, postRepository, commentRepository);
    }

    @Test
    @DisplayName("댓글 좋아요 요청 성공 확인")
    void likeComment_Success() {
        // When: 댓글 좋아요 요청 수행
        likeService.likeComment(new LikeRequest(commentId), memberId);

        // Then: 좋아요 저장 및 댓글 카운트 증가 확인
        verify(likeRepository).existsById(any(LikeId.class));
        verify(likeRepository).save(any(Like.class));
        verify(commentRepository).incrementLikeCount(commentId);
    }

    @Test
    @DisplayName("중복된 댓글 좋아요 요청 시 예외 발생 확인")
    void likeComment_AlreadyLiked_ThrowsException() {
        // Given: 이미 좋아요가 존재하는 상태
        when(likeRepository.existsById(any(LikeId.class))).thenReturn(true);

        // When & Then: 중복 요청 시 예외 발생
        assertThatThrownBy(() -> likeService.likeComment(new LikeRequest(commentId), memberId))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", LikeExceptionType.INVALID_ACCESS);
    }

    @Test
    @DisplayName("댓글 좋아요 취소 요청 성공 확인")
    void unlikeComment_Success() {
        // Given: 댓글 좋아요가 이미 추가된 상태
        when(likeRepository.existsById(any(LikeId.class))).thenReturn(true);

        // When: 댓글 좋아요 취소 요청 수행
        likeService.unlikeComment(new LikeRequest(commentId), memberId);

        // Then: 좋아요 삭제 및 댓글 카운트 감소 확인
        verify(likeRepository).delete(any(Like.class));
        verify(commentRepository).decrementLikeCount(commentId);
    }

    @Test
    @DisplayName("중복된 댓글 좋아요 취소 요청 시 예외 발생 확인")
    void unlikeComment_DuplicateThrowsException() {
        // Given: 댓글 좋아요가 존재하지 않는 상태
        when(likeRepository.existsById(any(LikeId.class))).thenReturn(false);

        // When & Then: 중복 취소 요청 시 예외 발생
        assertThatThrownBy(() -> likeService.unlikeComment(new LikeRequest(commentId), memberId))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", LikeExceptionType.INVALID_ACCESS);
    }
    
    @Test
    @DisplayName("게시글 좋아요 요청 성공 확인")
    void likePost_Success() {
        // Given: 좋아요가 없는 상태
        // When: 좋아요 요청 수행
        likeService.likePost(new LikeRequest(postId), memberId);

        // Then: 좋아요 저장 및 게시물 카운트 증가 확인
        verify(likeRepository).existsById(any(LikeId.class));
        verify(likeRepository).save(any(Like.class));
        verify(postRepository).incrementLikeCount(postId);
    }

    @Test
    @DisplayName("중복 좋아요 요청 시 예외 발생 확인")
    void likePost_AlreadyLiked_ThrowsException() {
        // Given: 이미 좋아요가 존재하는 상태
        when(likeRepository.existsById(any(LikeId.class))).thenReturn(true);

        // When & Then: 중복 요청 시 예외 발생
        assertThatThrownBy(() -> likeService.likePost(new LikeRequest(postId), memberId))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", LikeExceptionType.INVALID_ACCESS);
    }

    @Test
    @DisplayName("게시글 좋아요 취소 요청 성공 확인")
    void unlikePost_Success() {
        // Given: 이미 좋아요가 존재하는 상태
        when(likeRepository.existsById(any(LikeId.class))).thenReturn(true);

        // When: 좋아요 취소 요청 수행
        likeService.unlikePost(new LikeRequest(postId), memberId);

        // Then: 좋아요 삭제 및 게시물 카운트 감소 확인
        verify(likeRepository).delete(any(Like.class));
        verify(postRepository).decrementLikeCount(postId);
    }

    @Test
    @DisplayName("중복 좋아요 취소 요청 시 예외 발생 확인")
    void unlikePost_DuplicateThrowsException() {
        // Given: 좋아요가 존재하지 않는 상태
        when(likeRepository.existsById(any(LikeId.class))).thenReturn(false);

        // When & Then: 중복 요청 시 예외 발생
        assertThatThrownBy(() -> likeService.unlikePost(new LikeRequest(postId), memberId))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", LikeExceptionType.INVALID_ACCESS);
    }

}