package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.model.like.Like;
import deepdivers.community.domain.post.model.like.LikeId;
import deepdivers.community.domain.post.model.vo.LikeTarget;
import deepdivers.community.domain.post.repository.CommentRepository;
import deepdivers.community.domain.post.repository.LikeRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LikeServiceTest {

    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    private final Long memberId = 1L;
    private final Long postId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        likeService = new LikeService(likeRepository, postRepository, commentRepository);
    }

    private void mockLikeExistence(boolean exists) {
        when(likeRepository.existsById(any(LikeId.class))).thenReturn(exists);
    }

    @Test
    @DisplayName("게시글 좋아요 요청 성공 확인")
    void likePost_Success() {
        // Given: 좋아요가 없는 상태
        LikeRequest likeRequest = new LikeRequest(postId);
        when(likeRepository.existsById(any(LikeId.class))).thenReturn(false);

        // When: 좋아요 요청 수행
        likeService.likePost(likeRequest, memberId);

        // Then: 좋아요 저장 및 게시물 카운트 증가 확인
        verify(likeRepository).save(any(Like.class));
        verify(postRepository).incrementLikeCount(postId);
    }

    @Test
    @DisplayName("중복 좋아요 요청 시 예외 발생 확인")
    void likePost_AlreadyLiked_ThrowsException() {
        // Given: 이미 좋아요가 존재하는 상태
        LikeRequest likeRequest = new LikeRequest(postId);
        Like existingLike = Like.of(postId, memberId, LikeTarget.POST);
        when(likeRepository.findById(any(LikeId.class))).thenReturn(Optional.of(existingLike));

        // When & Then: 중복 요청 시 예외 발생
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> likeService.likePost(likeRequest, memberId)
        );

        assertEquals("유효하지 않은 접근입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("게시글 좋아요 취소 요청 성공 확인")
    void unlikePost_Success() {
        // Given: 이미 좋아요가 존재하는 상태
        LikeRequest likeRequest = new LikeRequest(postId);
        when(likeRepository.existsById(any(LikeId.class))).thenReturn(true);
        when(likeRepository.findById(any(LikeId.class))).thenReturn(Optional.of(Like.of(postId, memberId, LikeTarget.POST)));

        // When: 좋아요 취소 요청 수행
        likeService.unlikePost(likeRequest, memberId);

        // Then: 좋아요 삭제 및 게시물 카운트 감소 확인
        verify(likeRepository).delete(any(Like.class));
        verify(postRepository).decrementLikeCount(postId);
    }

    @Test
    @DisplayName("중복 좋아요 취소 요청 시 예외 발생 확인")
    void unlikePost_DuplicateThrowsException() {
        // Given: 좋아요가 존재하지 않는 상태
        mockLikeExistence(false);

        // When & Then: 중복 요청 시 예외 발생
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> likeService.unlikePost(new LikeRequest(postId), memberId)
        );

        assertEquals("유효하지 않은 접근입니다.", exception.getMessage());
    }
}