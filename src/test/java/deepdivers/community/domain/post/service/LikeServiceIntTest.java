package deepdivers.community.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.LikeExceptionType;
import deepdivers.community.domain.post.repository.LikeRepository;
import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.global.exception.model.BadRequestException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Transactional
@Import(LocalStackTestConfig.class)
class LikeServiceIntTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeRepository likeRepository;

    private Long memberId;
    private Long postId;
    private LikeRequest likeRequest;

    @BeforeEach
    void setUp() {
        memberId = 10L;
        postId = 10L;
        likeRequest = new LikeRequest(postId);
    }

    @Test
    @DisplayName("게시글 좋아요 기능 테스트")
    void likePost_SaveToDB() {
        // given test.sql
        // When: 좋아요 요청 수행
        NoContent result = likeService.likePost(likeRequest, memberId);

        // Then: 요청 성공 및 데이터베이스 반영 확인
        NoContent expect = NoContent.from(PostStatusType.POST_LIKE_SUCCESS);
        assertThat(result).isEqualTo(expect);
    }


    @Test
    @DisplayName("게시글 중복 좋아요 예외 테스트")
    void likePost_DuplicateThrows() {
        // Given: 좋아요 요청이 이미 수행된 상태
        likeService.likePost(likeRequest, memberId);

        // When & Then: 중복 요청 시 예외 발생 확인
        assertThatThrownBy(() -> likeService.likePost(likeRequest, memberId))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", LikeExceptionType.INVALID_ACCESS);
    }

    @Test
    @DisplayName("게시글 좋아요 취소 시 데이터베이스에서 삭제되는지 확인")
    void unlikePost_DeleteFromDB() {
        // Given: 좋아요가 이미 추가된 상태
        likeService.likePost(likeRequest, memberId);

        // When: 좋아요 취소 요청 수행
        NoContent result = likeService.unlikePost(likeRequest, memberId);

        // Then: 요청 성공 및 데이터베이스 삭제 확인
        NoContent expect = NoContent.from(PostStatusType.POST_UNLIKE_SUCCESS);
        assertThat(result).isEqualTo(expect);
    }

    @Test
    @DisplayName("중복된 좋아요 취소 요청 시 예외 발생 및 데이터베이스 변경 없음")
    void unlikePost_DuplicateThrows() {
        // Given: 좋아요가 이미 취소된 상태

        // When & Then: 중복 취소 요청 시 예외 발생 확인
        assertThatThrownBy(() -> likeService.unlikePost(likeRequest, memberId))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", LikeExceptionType.INVALID_ACCESS);
    }

}
