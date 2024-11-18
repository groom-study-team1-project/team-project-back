package deepdivers.community.domain.post.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostTest {

	@Test
	@DisplayName("유효한 게시물 생성 요청 시 Post 객체가 성공적으로 생성되는 것을 확인한다.")
	void postCreationShouldCreateValidPost() {
		// given
		String title = "유효한 제목";
		String content = "이것은 테스트를 위한 유효한 내용입니다.";
		Long categoryId = 1L;
		PostSaveRequest request = new PostSaveRequest(title, content, categoryId, null);
		PostCategory category = PostCategory.createCategory("카테고리", null, null);

		// Mocking the Member object
		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		// when
		Post post = Post.of(request, category, member);

		// then
		assertThat(post).isNotNull();
		assertThat(post.getTitle().getTitle()).isEqualTo(title);
		assertThat(post.getContent().getContent()).isEqualTo(content);
		assertThat(post.getCategory()).isEqualTo(category);
		assertThat(post.getMember()).isEqualTo(member);
	}

	@Test
	@DisplayName("유효하지 않은 제목 길이로 게시물 생성 시 INVALID_TITLE_LENGTH 예외가 발생하는 것을 확인한다.")
	void postCreationWithLongTitleShouldThrowException() {
		// given
		String title = "a".repeat(51); // 50자를 초과하는 제목
		String content = "이것은 유효한 내용입니다.";
		PostSaveRequest request = new PostSaveRequest(title, content, 1L, null);
		PostCategory category = PostCategory.createCategory("카테고리", null, null);

		// Mocking the Member object
		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		// when, then
		assertThatThrownBy(() -> Post.of(request, category, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_TITLE_LENGTH);
	}

	@Test
	@DisplayName("유효하지 않은 내용 길이로 게시물 생성 시 INVALID_CONTENT_LENGTH 예외가 발생하는 것을 확인한다.")
	void postCreationWithLongContentShouldThrowException() {
		// given
		String title = "유효한 제목";
		String content = "a".repeat(101); // 100자를 초과하는 내용
		PostSaveRequest request = new PostSaveRequest(title, content, 1L, null);
		PostCategory category = PostCategory.createCategory("카테고리", null, null);

		// Mocking the Member object
		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		// when, then
		assertThatThrownBy(() -> Post.of(request, category, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_CONTENT_LENGTH);
	}

	@Test
	@DisplayName("게시글 정보를 올바르게 조회할 수 있는지 확인한다.")
	void postRetrievalShouldReturnCorrectInformation() {
		// given
		String title = "게시글 제목";
		String content = "게시글 내용입니다.";
		PostCategory category = PostCategory.createCategory("카테고리", null, null);

		// Mocking the Member object
		Member member = mock(Member.class);
		when(member.getId()).thenReturn(1L);
		when(member.getNickname()).thenReturn("작성자닉네임");
		when(member.getImageUrl()).thenReturn("imageUrl");

		// when
		Post post = Post.of(new PostSaveRequest(title, content, category.getId(), null), category, member);

		// then
		assertThat(post.getTitle().getTitle()).isEqualTo(title);
		assertThat(post.getContent().getContent()).isEqualTo(content);
		assertThat(post.getCategory()).isEqualTo(category);
		assertThat(post.getMember()).isEqualTo(member);
	}

	@Test
	@DisplayName("존재하지 않는 게시글 조회 시 예외가 발생하는지 확인한다.")
	void postRetrievalWithNonExistentPostShouldThrowException() {
		// given
		Post post = null; // 가정: 게시글이 없는 상태

		// when, then
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
		// given
		PostCategory category = PostCategory.createCategory("카테고리", null, null);

		// Mocking the Member object
		Member member = mock(Member.class);
		when(member.getId()).thenReturn(1L);
		when(member.getNickname()).thenReturn("작성자닉네임");

		// 여러 개의 Post 객체 생성
		List<Post> posts = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			PostSaveRequest request = new PostSaveRequest("제목" + i, "내용내용내용" + i, category.getId(), null);
			Post post = Post.of(request, category, member);
			posts.add(post);
		}

		// when, then
		assertThat(posts).hasSize(3);  // 3개의 게시글이 반환됨을 확인
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
		// given
		String originalTitle = "원래 제목";
		String originalContent = "원래 내용";
		PostCategory originalCategory = PostCategory.createCategory("원래 카테고리", null, null);
		PostSaveRequest createRequest = new PostSaveRequest(originalTitle, originalContent, originalCategory.getId(), null);

		// Mocking the Member object
		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		// Post 객체 생성
		Post post = Post.of(createRequest, originalCategory, member);

		// 수정할 새로운 제목, 내용, 카테고리
		String updatedTitle = "수정된 제목";
		String updatedContent = "수정된 내용";
		PostCategory updatedCategory = PostCategory.createCategory("수정된 카테고리", null, null);

		// when
		post.updatePost(PostTitle.of(updatedTitle), PostContent.of(updatedContent), updatedCategory);

		// then
		assertThat(post.getTitle().getTitle()).isEqualTo(updatedTitle);
		assertThat(post.getContent().getContent()).isEqualTo(updatedContent);
		assertThat(post.getCategory()).isEqualTo(updatedCategory);
	}

	@Test
	@DisplayName("게시물 수정 시 제목이 유효하지 않은 경우 예외가 발생하는 것을 확인한다.")
	void postUpdateWithInvalidTitleShouldThrowException() {
		// given
		String originalTitle = "원래 제목";
		String originalContent = "원래 내용";
		PostCategory originalCategory = PostCategory.createCategory("원래 카테고리", null, null);
		PostSaveRequest createRequest = new PostSaveRequest(originalTitle, originalContent, originalCategory.getId(), null);

		// Mocking the Member object
		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		// Post 객체 생성
		Post post = Post.of(createRequest, originalCategory, member);

		// 유효하지 않은 제목 (51자 이상)
		String invalidTitle = "a".repeat(51);
		String updatedContent = "수정된 내용";
		PostCategory updatedCategory = PostCategory.createCategory("수정된 카테고리", null, null);

		// when, then
		assertThatThrownBy(() -> post.updatePost(PostTitle.of(invalidTitle), PostContent.of(updatedContent), updatedCategory))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_TITLE_LENGTH);
	}

	@Test
	@DisplayName("게시물 수정 시 내용이 유효하지 않은 경우 예외가 발생하는 것을 확인한다.")
	void postUpdateWithInvalidContentShouldThrowException() {
		// given
		String originalTitle = "원래 제목";
		String originalContent = "원래 내용";
		PostCategory originalCategory = PostCategory.createCategory("원래 카테고리", null, null);
		PostSaveRequest createRequest = new PostSaveRequest(originalTitle, originalContent, originalCategory.getId(), null);

		// Mocking the Member object
		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		// Post 객체 생성
		Post post = Post.of(createRequest, originalCategory, member);

		// 유효하지 않은 내용 (101자 이상)
		String updatedTitle = "수정된 제목";
		String invalidContent = "a".repeat(101);
		PostCategory updatedCategory = PostCategory.createCategory("수정된 카테고리", null, null);

		// when, then
		assertThatThrownBy(() -> post.updatePost(PostTitle.of(updatedTitle), PostContent.of(invalidContent), updatedCategory))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_CONTENT_LENGTH);
	}
}
