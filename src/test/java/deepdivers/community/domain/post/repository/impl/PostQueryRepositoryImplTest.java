package deepdivers.community.domain.post.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.RepositoryTest;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import deepdivers.community.domain.post.entity.PostSortType;
import deepdivers.community.domain.common.exception.NotFoundException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostQueryRepositoryImplTest extends RepositoryTest {

    @Test
    void 전쳬_게시글_목록을_가져온다() {
        // given, test.sql
        GetPostsRequest dto = new GetPostsRequest(null, null, null, null);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(null, dto);

        // then
        assertThat(result).hasSize(5);
    }

    @Test
    @DisplayName("카테고리 정보만 있을 경우 카테고리 별 삭제되지 않은 전체 게시글 목록이 조회가 된다.")
    void givenNullLastPostIdAndCategoryIdWhenFindAllPostsThenReturnPostsByCategory() {
        // given, test.sql
        GetPostsRequest dto = new GetPostsRequest(1L, null, null, null);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(null, dto);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("마지막 포스트 정보만 있을 경우 모든 카테고리의 삭제되지 않은 게시글 목록이 조회가 된다.")
    void givenLastPostIdAndNullCategoryIdWhenFindAllPostsThenReturnPostsAmongPostIdSmallerThanLastPostId() {
        // given, test.sql
        GetPostsRequest dto = new GetPostsRequest(null, 5L, null, null);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(null, dto);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("마지막 포스트 정보와 카테 고리 정보가 있을 경우 카테고리 별 삭제되지 않은 게시글 목록이 조회가 된다.")
    void givenLastPostIdAndCategoryIdWhenFindAllPostsThenReturnNoDeletePosts() {
        // given, test.sql
        GetPostsRequest dto = new GetPostsRequest(1L, 5L, null, null);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(null, dto);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("마지막 게시글 id가 1번일 경우, 조회 시 0개가 조회된다.")
    void givenLastPostIdWhenFindAllPostsThenReturnEmptyPosts() {
        // given
        GetPostsRequest dto = new GetPostsRequest(null, 1L, null, null);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(null, dto);

        // then
        assertThat(result).hasSize(0);
    }

    @Test
    void 특정_사용자의_게시글_작성_목록을_가져올_수_있다() {
        // given
        GetPostsRequest dto = new GetPostsRequest(null, null, null, null);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(1L, dto);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void 게시글_상세_조회를_할_수_있다() {
        // given
        Long postId = 1L;
        Long viewerId = 1L;

        // when
        PostDetailResponse result = postQueryRepository.readPostByPostId(postId, viewerId);

        // then
        assertThat(result.getPostId()).isEqualTo(postId);
    }

    @Test
    void 게시글_조회자가_게시글_작성자라면_wroteMe_정보가_true이다() {
        // given, test.sql
        Long postId = 1L;
        Long viewerId = 1L;

        // when
        PostDetailResponse result = postQueryRepository.readPostByPostId(postId, viewerId);

        // then
        assertThat(result.isWroteMe()).isTrue();
    }

    @Test
    void 게시글_조회자가_게시글_작성자가_아니라면_wroteMe_정보가_false이다() {
        // given, test.sql
        Long postId = 1L;
        Long viewerId = 0L;

        // when
        PostDetailResponse result = postQueryRepository.readPostByPostId(postId, viewerId);

        // then
        assertThat(result.isWroteMe()).isFalse();
    }

    @Test
    void 게시글_조회자가_좋아요를_눌렀을_경우_likedMe가_true이다() {
        // given, test.sql
        Long postId = 1L;
        Long viewerId = 1L;

        // when
        PostDetailResponse result = postQueryRepository.readPostByPostId(postId, viewerId);

        // then
        assertThat(result.isLikedMe()).isTrue();
    }

    @Test
    void 게시글_조회자가_좋아요를_누르지_않았을_경우_likedMe가_false이다() {
        // given, test.sql
        Long postId = 1L;
        Long viewerId = 0L;

        // when
        PostDetailResponse result = postQueryRepository.readPostByPostId(postId, viewerId);

        // then
        assertThat(result.isLikedMe()).isFalse();
    }

    @Test
    void 존재하지_않는_게시글_조회시_예외가_발생한다() {
        // given, test.sql
        Long postId = 15L;
        Long viewerId = 1L;

        // when & then
        assertThatThrownBy(() -> postQueryRepository.readPostByPostId(postId, viewerId))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.POST_NOT_FOUND);
    }

    @Test
    void 게시글_조회_수를_기준으로_조회할_수_있다() {
        // given
        GetPostsRequest dto = new GetPostsRequest(null, null, PostSortType.HOT, null);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(null, dto);

        // then
        assertThat(result.getFirst().getPostId()).isEqualTo(8);
    }

    @Test
    void 게시글_댓글_수를_기준으로_조회할_수_있다() {
        // given
        GetPostsRequest dto = new GetPostsRequest(null, null, PostSortType.COMMENT, null);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(null, dto);

        // then
        assertThat(result.getFirst().getCommentCount()).isEqualTo(2);
    }

    @Test
    void 게시글_인기_수를_기준으로_조회할_수_있다() {
        // given
        GetPostsRequest dto = new GetPostsRequest(null, null, PostSortType.LATEST, null);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(null, dto);

        // then
        assertThat(result.getFirst().getPostId()).isEqualTo(10);
    }

    @Test
    void 최대_조회개수가_정해져있다() {
        // given
        GetPostsRequest dto = new GetPostsRequest(null, null, PostSortType.LATEST, 100);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(null, dto);

        // then
        assertThat(result).hasSizeLessThan(31);
    }

    @Test
    void 최소_최대_조회개수_사이만큼_조회가_된다() {
        // given
        GetPostsRequest dto = new GetPostsRequest(null, null, PostSortType.LATEST, 7);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(null, dto);

        // then --> deleted data 제외된 결과 값
        assertThat(result).hasSize(6);
    }

    @Test
    void 최소_limit_제한이_있다() {
        // given, test.sql
        GetPostsRequest dto = new GetPostsRequest(null, null, null, 2);

        // when
        List<PostPreviewResponse> result = postQueryRepository.findAllPosts(null, dto);

        // then
        assertThat(result).hasSize(5);
    }

}