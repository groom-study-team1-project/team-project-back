package deepdivers.community.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import deepdivers.community.domain.IntegrationTest;
import deepdivers.community.domain.category.exception.CategoryExceptionCode;
import deepdivers.community.domain.common.PostRequestFactory;
import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.common.exception.NotFoundException;
import deepdivers.community.domain.file.repository.entity.File;
import deepdivers.community.domain.file.repository.entity.FileType;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.post.entity.Post;
import deepdivers.community.domain.post.entity.PostStatus;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProjectPostServiceTest extends IntegrationTest {

    @Autowired private ProjectPostService projectPostService;

    @BeforeEach
    void setUp() {
        createTestObject("default-image/posts/thumbnail.png");
        createTestObject("posts/image2.png");
        createTestObject("posts/image3.png");
        createTestObject("posts/image4.png");
        createTestObject("posts/image5.png");
    }


    @Test
    @DisplayName("게시글 생성이 성공하면 저장된 게시글 정보를 테스트한다.")
    void createPostSuccessTest() {
        // Given
        ProjectPostRequest request = new ProjectPostRequest("title", "Content", "Thumbnail", 2L, List.of(), List.of(), List.of());
        Member member = getMember(1L);

        // When
        API<Long> response = projectPostService.createProjectPost(member, request);

        // Then
        Long id = response.result();
        assertThat(getPost(id)).isNotNull();
    }

    @Test
    @DisplayName("게시글 수정이 성공하면 수정된 정보를 반환한다")
    void updateSuccessTest() {
        // Given
        Member member = getMember(2L);
        ProjectPostRequest request = new ProjectPostRequest("Title", "Content", "Thumbnail", 2L, List.of(), List.of(), List.of());
        createTestObject("default-image/posts/thumbnail.png");

        // When
        projectPostService.updateProjectPost(2L, member, request);

        // Then
        Post updatedPost = getPost(2L);
        assertThat(updatedPost.getTitle().getTitle()).isEqualTo("Title");
    }

    @Test
    @DisplayName("게시글 삭제 요청이 성공적으로 처리되면 상태가 'DELETED'로 변경된다")
    void deletePostSuccessTest() {
        // given
        Member member = getMember(2L);

        // When
        projectPostService.deletePost(2L, member);

        // Then
        Post deletedPost = getPost(2L);
        assertThat(deletedPost.getStatus()).isEqualTo(PostStatus.DELETED);
    }

    @Test
    void 게시글_삭제_요청자와_게시글_작성자가_다를_경우_예외가_발생한다() {
        // given
        Member member = getMember(1L);

        // When & then
        assertThatThrownBy(() -> projectPostService.deletePost(2L, member))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.NOT_POST_AUTHOR);
    }

    @Test
    void 존재하지_않는_게시글일_경우_예외가_발생한다() {
        // given
        Member member = getMember(1L);

        // When & then
        assertThatThrownBy(() -> projectPostService.deletePost(999L, member))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.POST_NOT_FOUND);
    }

}