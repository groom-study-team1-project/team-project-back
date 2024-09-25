package deepdivers.community.domain.post.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PostTest {

	@Test
	@DisplayName("유효한 게시물 생성 요청 시 Post 객체가 성공적으로 생성되는 것을 확인한다.")
	void postCreationShouldCreateValidPost() {
		// given
		String title = "유효한 제목";
		String content = "이것은 테스트를 위한 유효한 내용입니다.";
		Long categoryId = 1L;
		PostCreateRequest request = new PostCreateRequest(title, content, categoryId, null);
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
		PostCreateRequest request = new PostCreateRequest(title, content, 1L, null);
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
		PostCreateRequest request = new PostCreateRequest(title, content, 1L, null);
		PostCategory category = PostCategory.createCategory("카테고리", null, null);

		// Mocking the Member object
		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		// when, then
		assertThatThrownBy(() -> Post.of(request, category, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_CONTENT_LENGTH);
	}
}
