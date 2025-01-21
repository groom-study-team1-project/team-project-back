package deepdivers.community.domain.post.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.RepositoryTest;
import deepdivers.community.domain.common.exception.NotFoundException;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.ProjectPostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPreviewResponse;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProjectPostQueryRepositoryImplTest extends RepositoryTest {

    @Test
    void 프로젝트_게시글을_가져올_수_있다() {
        // given
        GetPostsRequest dto = new GetPostsRequest(null, null, null, null, null, null);

        // when
        List<ProjectPostPreviewResponse> result = projectPostQueryRepository.findAllPosts(null, dto);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    void 사용자별_프로젝트_게시글을_가져올_수_있다() {
        // given
        GetPostsRequest dto = new GetPostsRequest(null, null, null, null, null, null);

        // when
        List<ProjectPostPreviewResponse> result = projectPostQueryRepository.findAllPosts(2L, dto);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void 프로젝트_게시글_상세_정보를_가져올_수_있다() {
        // given
        // when
        ProjectPostDetailResponse result = projectPostQueryRepository.readPostByPostId(1L, 1L);

        // then
        assertThat(result.getSlideImageUrls()).hasSize(0);
    }

    @Test
    void 존재하지_않는_게시글_조회시_예외가_발생한다() {
        // given, test.sql
        Long postId = 15L;
        Long viewerId = 1L;

        // when & then
        assertThatThrownBy(() -> projectPostQueryRepository.readPostByPostId(postId, viewerId))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.POST_NOT_FOUND);
    }

}