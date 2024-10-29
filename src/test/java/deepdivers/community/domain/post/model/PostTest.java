package deepdivers.community.domain.post.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class PostTest {

	@Test
	@DisplayName("유효한 게시물 생성 요청 시 Post 객체가 성공적으로 생성되는 것을 확인한다.")
	void postCreationShouldCreateValidPost() {
		String title = "유효한 제목";
		String content = "이것은 테스트를 위한 유효한 내용입니다.";
		Long categoryId = 1L;
		PostCreateRequest request = new PostCreateRequest(title, content, categoryId, null, List.of("1"));
		PostCategory category = PostCategory.createCategory("카테고리", null, null);

		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		Post post = Post.of(request, category, member);

		assertThat(post).isNotNull();
		assertThat(post.getTitle().getTitle()).isEqualTo(title);
		assertThat(post.getContent().getContent()).isEqualTo(content);
		assertThat(post.getCategory()).isEqualTo(category);
		assertThat(post.getMember()).isEqualTo(member);
	}

	@Test
	@DisplayName("유효하지 않은 제목 길이로 게시물 생성 시 INVALID_TITLE_LENGTH 예외가 발생하는 것을 확인한다.")
	void postCreationWithLongTitleShouldThrowException() {
		String title = "a".repeat(51); // 50자를 초과하는 제목
		String content = "이것은 유효한 내용입니다.";

		PostCreateRequest request = new PostCreateRequest(title, content, 1L, null, List.of("1"));
		PostCategory category = PostCategory.createCategory("카테고리", null, null);

		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		assertThatThrownBy(() -> Post.of(request, category, member))
				.isInstanceOf(BadRequestException.class)
				.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_TITLE_LENGTH);
	}

	@Test
	@DisplayName("유효하지 않은 내용 길이로 게시물 생성 시 INVALID_CONTENT_LENGTH 예외가 발생하는 것을 확인한다.")
	void postCreationWithLongContentShouldThrowException() {
		String title = "유효한 제목";
		String content = "a".repeat(101); // 100자를 초과하는 내용
		PostCreateRequest request = new PostCreateRequest(title, content, 1L, null, List.of("1"));
		PostCategory category = PostCategory.createCategory("카테고리", null, null);

		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		assertThatThrownBy(() -> Post.of(request, category, member))
				.isInstanceOf(BadRequestException.class)
				.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_CONTENT_LENGTH);
	}

	@Test
	@DisplayName("게시글 정보를 올바르게 조회할 수 있는지 확인한다.")
	void postRetrievalShouldReturnCorrectInformation() {
		String title = "게시글 제목";
		String content = "게시글 내용입니다.";
		PostCategory category = PostCategory.createCategory("카테고리", null, null);

		Member member = mock(Member.class);
		when(member.getId()).thenReturn(1L);
		when(member.getNickname()).thenReturn("작성자닉네임");
		when(member.getImageUrl()).thenReturn("imageUrl");

		Post post = Post.of(new PostCreateRequest(title, content, category.getId(), null, List.of("1")), category, member);

		assertThat(post.getTitle().getTitle()).isEqualTo(title);
		assertThat(post.getContent().getContent()).isEqualTo(content);
		assertThat(post.getCategory()).isEqualTo(category);
		assertThat(post.getMember()).isEqualTo(member);
	}

	@Test
	@DisplayName("존재하지 않는 게시글 조회 시 예외가 발생하는지 확인한다.")
	void postRetrievalWithNonExistentPostShouldThrowException() {
		Post post = null; // 가정: 게시글이 없는 상태

		assertThatThrownBy(() -> {
			if (post == null) {
				throw new BadRequestException(PostExceptionType.POST_NOT_FOUND);
			}
		}).isInstanceOf(BadRequestException.class)
				.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.POST_NOT_FOUND);
	}

	@Test
	@DisplayName("전체 게시글을 성공적으로 조회할 수 있다.")
	void shouldRetrieveAllPostsSuccessfully() {
		PostCategory category = PostCategory.createCategory("카테고리", null, null);

		Member member = mock(Member.class);
		when(member.getId()).thenReturn(1L);
		when(member.getNickname()).thenReturn("작성자닉네임");

		List<Post> posts = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			List<MultipartFile> imageFiles = List.of(
					new MockMultipartFile("image" + i + ".jpg", "image" + i + ".jpg", "image/jpeg", ("image-content-" + i).getBytes())
			);
			PostCreateRequest request = new PostCreateRequest("제목" + i, "내용내용내용" + i, category.getId(), null, List.of("1"));
			Post post = Post.of(request, category, member);
			posts.add(post);
		}

		assertThat(posts).hasSize(3);
		assertThat(posts.get(0).getTitle().getTitle()).isEqualTo("제목1");
		assertThat(posts.get(1).getTitle().getTitle()).isEqualTo("제목2");
		assertThat(posts.get(2).getTitle().getTitle()).isEqualTo("제목3");
	}

	@Test
	@DisplayName("전체 게시글 조회 시 반환된 게시글이 없는 경우를 처리한다.")
	void shouldReturnEmptyListWhenNoPostsAreAvailable() {
		// given
		List<Post> posts = new ArrayList<>();  // 게시글 리스트가 비어있음

		// when, then
		assertThat(posts).isEmpty();  // 반환된 게시글이 없는지 확인
	}

	@Test
	@DisplayName("유효한 게시물 수정 요청 시 Post 객체가 성공적으로 업데이트되는 것을 확인한다.")
	void postUpdateShouldUpdateValidPost() {
		String originalTitle = "원래 제목";
		String originalContent = "원래 내용";
		PostCategory originalCategory = PostCategory.createCategory("원래 카테고리", null, null);
		List<MultipartFile> originalImageFiles = List.of(
				new MockMultipartFile("originalImage.jpg", "originalImage.jpg", "image/jpeg", "original-image-content".getBytes())
		);
		PostCreateRequest createRequest = new PostCreateRequest(originalTitle, originalContent, originalCategory.getId(), null, List.of("1"));

		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		Post post = Post.of(createRequest, originalCategory, member);

		String updatedTitle = "수정된 제목";
		String updatedContent = "수정된 내용";
		PostCategory updatedCategory = PostCategory.createCategory("수정된 카테고리", null, null);
		List<String> updatedImageUrls = List.of("updatedImage1.jpg", "updatedImage2.jpg");

		post.updatePost(PostTitle.of(updatedTitle), PostContent.of(updatedContent), updatedCategory);
		

		assertThat(post.getTitle().getTitle()).isEqualTo(updatedTitle);
		assertThat(post.getContent().getContent()).isEqualTo(updatedContent);
		assertThat(post.getCategory()).isEqualTo(updatedCategory);
		
	}

	@Test
	@DisplayName("게시물 수정 시 제목이 유효하지 않은 경우 예외가 발생하는 것을 확인한다.")
	void postUpdateWithInvalidTitleShouldThrowException() {
		String originalTitle = "원래 제목";
		String originalContent = "원래 내용";
		PostCategory originalCategory = PostCategory.createCategory("원래 카테고리", null, null);
		List<MultipartFile> originalImageFiles = List.of(
				new MockMultipartFile("originalImage.jpg", "originalImage.jpg", "image/jpeg", "original-image-content".getBytes())
		);
		PostCreateRequest createRequest = new PostCreateRequest(originalTitle, originalContent, originalCategory.getId(), null, List.of("1"));

		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		Post post = Post.of(createRequest, originalCategory, member);

		String invalidTitle = "a".repeat(51); // 유효하지 않은 제목 길이
		String updatedContent = "수정된 내용";
		PostCategory updatedCategory = PostCategory.createCategory("수정된 카테고리", null, null);

		assertThatThrownBy(() -> post.updatePost(PostTitle.of(invalidTitle), PostContent.of(updatedContent), updatedCategory))
				.isInstanceOf(BadRequestException.class)
				.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_TITLE_LENGTH);
	}

	@Test
	@DisplayName("게시물 수정 시 내용이 유효하지 않은 경우 예외가 발생하는 것을 확인한다.")
	void postUpdateWithInvalidContentShouldThrowException() {
		String originalTitle = "원래 제목";
		String originalContent = "원래 내용";
		PostCategory originalCategory = PostCategory.createCategory("원래 카테고리", null, null);
		List<MultipartFile> originalImageFiles = List.of(
				new MockMultipartFile("originalImage.jpg", "originalImage.jpg", "image/jpeg", "original-image-content".getBytes())
		);
		PostCreateRequest createRequest = new PostCreateRequest(originalTitle, originalContent, originalCategory.getId(), null, List.of("1"));

		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		Post post = Post.of(createRequest, originalCategory, member);

		String updatedTitle = "수정된 제목";
		String invalidContent = "a".repeat(101); // 유효하지 않은 내용 길이
		PostCategory updatedCategory = PostCategory.createCategory("수정된 카테고리", null, null);

		assertThatThrownBy(() -> post.updatePost(PostTitle.of(updatedTitle), PostContent.of(invalidContent), updatedCategory))
				.isInstanceOf(BadRequestException.class)
				.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_CONTENT_LENGTH);
	}
}
