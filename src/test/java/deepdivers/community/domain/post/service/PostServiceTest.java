package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.response.PostImageUploadResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.PostImage;
import deepdivers.community.domain.post.model.vo.CategoryStatus;
import deepdivers.community.domain.post.model.vo.PostStatus;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.encryptor.Encryptor;
import deepdivers.community.global.utility.encryptor.EncryptorBean;
import deepdivers.community.infra.aws.s3.exception.S3Exception;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DirtiesContext
@Transactional
@Import(LocalStackTestConfig.class)
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    @EncryptorBean
    private Encryptor encryptor;

    private Member member;
    private PostCategory category;
    private Post post;

    @BeforeEach
    void setUp() {
        MemberSignUpRequest signUpRequest = new MemberSignUpRequest(
                "test@mail.com",
                "password123*",
                "nickname",
                "http://image.url",
                "010-1234-5678"
        );
        member = Member.of(signUpRequest, encryptor);
        memberRepository.save(member);

        category = PostCategory.createCategory("Category", "Description", CategoryStatus.ACTIVE);
        categoryRepository.save(category);

        post = Post.of(
                new PostSaveRequest("Post Title", "Post Content", "Thumbnail",  category.getId(), List.of("tag1", "tag2"), List.of("http/temp/f.jpeg")),
                category,
                member
        );
        postRepository.save(post);
    }

    @Test
    @DisplayName("이미지 업로드 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    void uploadPostImageSuccessfullyReturns200OK() throws Exception {
        // given
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test Image Content".getBytes()
        );

        // when
        API<PostImageUploadResponse> response = postService.uploadPostImage(imageFile);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getResult().imageUrl()).contains("temp/");
    }

    @Test
    @DisplayName("유효하지 않은 파일로 업로드 요청 시 예외가 발생한다")
    void uploadPostImageWithInvalidFileThrowsException() throws Exception {
        // given
        MockMultipartFile invalidFile = new MockMultipartFile(
                "imageFile",
                "invalid.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Invalid File Content".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> postService.uploadPostImage(invalidFile))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(S3Exception.INVALID_IMAGE_FORMAT.getMessage());
    }

    @Test
    @DisplayName("게시글 생성이 성공하면 저장된 게시글 정보를 테스트한다.")
    void createPostSuccessTest() {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "f.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test Image Content".getBytes()
        );
        API<PostImageUploadResponse> imageUploadResponse = postService.uploadPostImage(imageFile);
        PostSaveRequest request = new PostSaveRequest(
                "Post Title",
                "Post Content",
                "Thumbnail",
                category.getId(),
                List.of("tag1", "tag2"),
                List.of(imageUploadResponse.getResult().imageUrl())
        );

        // When
        API<PostSaveResponse> response = postService.createPost(request, member);

        // Then
        Post savedPost = postRepository.findById(response.result().postId()).orElse(null);
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getTitle().getTitle()).isEqualTo("Post Title");
        assertThat(savedPost.getContent().getContent()).isEqualTo("Post Content");
        assertThat(savedPost.getHashtags()).hasSize(2);
        assertThat(savedPost.getPostImages())
                .extracting(PostImage::getImageUrl)
                .anyMatch(url -> url.contains("posts/"));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 ID로 게시글 생성 요청 시 예외가 발생한다.")
    void createPostWithInvalidCategoryThrowsException() {
        // Given
        PostSaveRequest request = new PostSaveRequest(
                "Post Title",
                "Post Content",
                "Thumbnail",
                999L,
                List.of("tag1", "tag2"),
                List.of("http/temp/f.jpeg")
        );

        // When, Then
        assertThatThrownBy(() -> postService.createPost(request, member))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionType.CATEGORY_NOT_FOUND);
    }

    @Test
    @DisplayName("유효하지 않은 해시태그가 포함된 게시글 생성 요청 시 예외가 발생한다.")
    void createPostWithInvalidHashtagsThrowsException() {
        // Given
        PostSaveRequest request = new PostSaveRequest(
                "Post Title",
                "Post Content",
                "Thumbnail",
                category.getId(),
                List.of("tag1", "invalid#tag"),
                List.of("http/temp/f.jpeg")
        );

        // When, Then
        assertThatThrownBy(() -> postService.createPost(request, member))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", HashtagExceptionType.INVALID_HASHTAG_FORMAT);
    }

    @Test
    @DisplayName("게시글 수정이 성공하면 수정된 정보를 반환한다")
    void updatePostSuccessTest() {
        // Given
        PostCategory newCategory = PostCategory.createCategory("Updated Category", "Updated Description", CategoryStatus.ACTIVE);
        categoryRepository.save(newCategory);

        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "f.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test Image Content".getBytes()
        );
        API<PostImageUploadResponse> imageUploadResponse = postService.uploadPostImage(imageFile);
        PostSaveRequest request = new PostSaveRequest(
                "Updated Title",
                "Updated Content",
                "Updated Thumbnail",
                newCategory.getId(),
                List.of("newTag1", "newTag2"),
                List.of(imageUploadResponse.getResult().imageUrl())
        );

        // When
        postService.updatePost(post.getId(), request, member);

        // Then
        Post updatedPost = postRepository.findById(post.getId()).orElse(null);

        assertThat(updatedPost).isNotNull();
        assertThat(updatedPost.getTitle().getTitle()).isEqualTo("Updated Title");
        assertThat(updatedPost.getContent().getContent()).isEqualTo("Updated Content");
        assertThat(updatedPost.getCategory().getName()).isEqualTo("Updated Category");
        assertThat(updatedPost.getHashtags()).hasSize(2);
        assertThat(updatedPost.getPostImages())
                .extracting(PostImage::getImageUrl)
                .anyMatch(url -> url.contains("posts/"));
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID로 수정 요청 시 예외가 발생한다")
    void updateNonExistentPostThrowsException() {
        // Given
        PostSaveRequest request = new PostSaveRequest(
                "Updated Title",
                "Updated Content",
                "Updated Thumbnail",
                category.getId(),
                List.of("newTag1", "newTag2"),
                List.of("http/temp/f.jpeg")
        );

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(999L, request, member))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 작성자가 아닌 멤버가 수정 요청 시 예외가 발생한다")
    void updatePostByNonAuthorThrowsException() {
        // Given
        Member anotherMember = Member.of(
                new MemberSignUpRequest(
                        "other@mail.com",
                        "password123*",
                        "otherNickname",
                        "http://image.url",
                        "010-5678-1234"
                ),
                encryptor
        );

        PostSaveRequest request = new PostSaveRequest(
                "Updated Title",
                "Updated Content",
                "Updated Thumbnail",
                category.getId(),
                List.of("newTag1", "newTag2"),
                List.of("http/temp/f.jpeg")
        );

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(post.getId(), request, anotherMember))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.NOT_POST_AUTHOR);
    }

    @Test
    @DisplayName("수정 요청에서 존재하지 않는 카테고리를 지정할 경우 예외가 발생한다")
    void updatePostWithInvalidCategoryThrowsException() {
        // Given
        PostSaveRequest request = new PostSaveRequest(
                "Updated Title",
                "Updated Content",
                "Updated Thumbnail",
                999L,
                List.of("newTag1", "newTag2"),
                List.of("http/temp/f.jpeg")
        );

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(post.getId(), request, member))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionType.CATEGORY_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 삭제 요청이 성공적으로 처리되면 상태가 'DELETED'로 변경된다")
    void deletePostSuccessTest() {
        // When
        NoContent response = postService.deletePost(post.getId(), member);

        // Then
        Post deletedPost = postRepository.findById(post.getId()).orElse(null);
        assertThat(deletedPost).isNotNull();
        assertThat(deletedPost.getStatus()).isEqualTo(PostStatus.DELETED);
        assertThat(response.status().code()).isEqualTo(PostStatusType.POST_DELETE_SUCCESS.getCode());
        assertThat(response.status().message()).isEqualTo(PostStatusType.POST_DELETE_SUCCESS.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID로 삭제 요청 시 예외가 발생한다")
    void deletePostWithInvalidIdThrowsException() {
        // Given
        Long invalidPostId = 999L;

        // When & Then
        assertThatThrownBy(() -> postService.deletePost(invalidPostId, member))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 작성자가 아닌 멤버가 삭제 요청 시 예외가 발생한다")
    void deletePostByNonAuthorThrowsException() {
        // Given
        Member anotherMember = Member.of(
                new MemberSignUpRequest(
                        "other@mail.com",
                        "password123*",
                        "otherNickname",
                        "http://image.url",
                        "010-5678-1234"
                ),
                encryptor
        );

        // When & Then
        assertThatThrownBy(() -> postService.deletePost(post.getId(), anotherMember))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.NOT_POST_AUTHOR);
    }

    @Test
    @DisplayName("게시글 상세 조회가 성공적으로 처리되면 게시글 상세 정보를 반환한다")
    void readPostDetailSuccessTest() {
        // Given
        String ipAddr = "127.0.0.1";

        // When
        API<PostReadResponse> response = postService.readPostDetail(post.getId(), ipAddr);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getResult().title()).isEqualTo("Post Title");
        assertThat(response.getResult().content()).isEqualTo("Post Content");
    }

    @Test
    @DisplayName("존재하지 않는 게시글 ID로 상세 조회 시 예외가 발생한다")
    void readPostDetailWithInvalidIdThrowsException() {
        // Given
        Long invalidPostId = 999L;
        String ipAddr = "127.0.0.1";

        // When & Then
        assertThatThrownBy(() -> postService.readPostDetail(invalidPostId, ipAddr))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.POST_NOT_FOUND);
    }

}
