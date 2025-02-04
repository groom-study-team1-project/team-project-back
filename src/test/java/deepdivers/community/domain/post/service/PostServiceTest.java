package deepdivers.community.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.IntegrationTest;
import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.exception.NotFoundException;
import deepdivers.community.domain.file.repository.entity.File;
import deepdivers.community.domain.file.repository.entity.FileType;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.category.exception.CategoryExceptionCode;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import deepdivers.community.domain.post.entity.Post;
import deepdivers.community.domain.post.entity.PostStatus;
import deepdivers.community.domain.common.exception.BadRequestException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostServiceTest extends IntegrationTest {

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("게시글 생성이 성공하면 저장된 게시글 정보를 테스트한다.")
    void createPostSuccessTest() {
        // Given
        PostSaveRequest request = new PostSaveRequest("title", "Content", "Thumbnail", 1L, List.of(), List.of());
        Member member = getMember(1L);

        // When
        API<PostSaveResponse> response = postService.createPost(request, member);

        // Then
        Long id = response.result().postId();
        assertThat(getPost(id)).isNotNull();
    }

    @Test
    void 게시글_작성_후_사용자_게시글_수가_증가한다() {
        // Given
        PostSaveRequest request = new PostSaveRequest("title", "Content", "Thumbnail", 1L, List.of(), List.of());
        Member member = getMember(1L);
        Integer postCount = member.getActivityStats().getPostCount();

        // When
        postService.createPost(request, member);

        // Then
        assertThat(member.getActivityStats().getPostCount()).isEqualTo(postCount + 1);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 ID로 게시글 생성 요청 시 예외가 발생한다.")
    void createPostWithInvalidCategoryThrowsException() {
        // Given, test.sql
        PostSaveRequest request = new PostSaveRequest("Title", "Content", "Thumbnail", 999L, List.of(), List.of());
        Member member = getMember(1L);

        // When, Then
        assertThatThrownBy(() -> postService.createPost(request, member))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionCode.CATEGORY_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 수정이 성공하면 수정된 정보를 반환한다")
    void updateSuccessTest() {
        // Given
        Member member = getMember(1L);
        PostSaveRequest request = new PostSaveRequest("Title", "Content", "Thumbnail", 3L, List.of(), List.of());
        createTestObject("default-image/posts/thumbnail.png");

        // When
        postService.updatePost(1L, request, member);

        // Then
        Post updatedPost = getPost(1L);
        assertThat(updatedPost.getCategory().getId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID로 수정 요청 시 예외가 발생한다")
    void updateNonExistentPostThrowsException() {
        // Given
        PostSaveRequest request = new PostSaveRequest("Title", "Content", "Thumbnail", 2L, List.of(), List.of());
        Member member = getMember(1L);

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(999L, request, member))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 작성자가 아닌 멤버가 수정 요청 시 예외가 발생한다")
    void updateByNonAuthorThrowsException() {
        // Given
        Member member = getMember(2L);
        PostSaveRequest request = new PostSaveRequest("Title", "Content", "Thumbnail", 2L, List.of(), List.of());

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(1L, request, member))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.NOT_POST_AUTHOR);
    }

    @Test
    @DisplayName("수정 요청에서 존재하지 않는 카테고리를 지정할 경우 예외가 발생한다")
    void updateWithInvalidCategoryThrowsException() {
        // Given
        Member member = getMember(1L);
        PostSaveRequest request = new PostSaveRequest("Title", "Content", "Thumbnail", 5L, List.of(), List.of());

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(1L, request, member))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionCode.CATEGORY_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 삭제 요청이 성공적으로 처리되면 상태가 'DELETED'로 변경된다")
    void deletePostSuccessTest() {
        // given
        Member member = getMember(1L);

        // When
        postService.deletePost(1L, member);

        // Then
        Post deletedPost = getPost(1L);
        assertThat(deletedPost.getStatus()).isEqualTo(PostStatus.DELETED);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID로 삭제 요청 시 예외가 발생한다")
    void deletePostWithInvalidIdThrowsException() {
        // Given
        Long invalidPostId = 999L;
        Member member = getMember(1L);

        // When & Then
        assertThatThrownBy(() -> postService.deletePost(invalidPostId, member))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 작성자가 아닌 멤버가 삭제 요청 시 예외가 발생한다")
    void deletePostByNonAuthorThrowsException() {
        // Given
        Member member = getMember(2L);

        // When & Then
        assertThatThrownBy(() -> postService.deletePost(1L, member))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.NOT_POST_AUTHOR);
    }

    @Test
    @DisplayName("이미지가 업로드된 게시글을 작성한다.")
    void createHavingImagePostSuccess() {
        // Given
        createTestObject("posts/image.png");
        PostSaveRequest request = new PostSaveRequest("title", "Content", "", 1L, List.of(), List.of("posts/image.png"));
        Member member = getMember(1L);

        // When
        API<PostSaveResponse> response = postService.createPost(request, member);

        // Then
        Long id = response.result().postId();
        Post post = getPost(id);
        assertThat(post).isNotNull();
    }

    @Test
    @DisplayName("이미지가 업로드된 게시글을 수정한다.")
    void editHavingImagePostSuccess() {
        // Given
        createTestObject("default-image/posts/thumbnail.png");
        createTestObject("posts/image2.png");
        createTestObject("posts/image3.png");
        PostSaveRequest request = new PostSaveRequest("title", "Content", "", 1L, List.of(), List.of("posts/image2.png", "posts/image3.png"));
        Member member = getMember(1L);

        // When
        postService.updatePost(1L, request, member);

        // Then
        List<File> postContentFiles = getPostImages(1L, FileType.POST_CONTENT);
        List<String> imageKeys = postContentFiles.stream().map(File::getFileKey).toList();
        assertThat(imageKeys).isEqualTo(List.of("posts/image2.png", "posts/image3.png"));
    }

}
