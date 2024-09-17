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
		PostCategory category = PostCategory.builder().title("카테고리").build();

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

	@ParameterizedTest
	@CsvSource({
		"'', 이것은 유효한 내용입니다.",
		"짧, 이것은 유효한 내용입니다.",
		"'이 제목은 너무 길어서 유효하지 않습니다. 제목의 최대 길이는 50자이며, 이 제목은 그 제한을 초과합니다. 이 제목은 너무 길어서 유효하지 않습니다. 제목의 최대 길이는 50자이며, 이 제목은 그 제한을 초과합니다.이 제목은 너무 길어서 유효하지 않습니다. 제목의 최대 길이는 50자이며, 이 제목은 그 제한을 초과합니다.이 제목은 너무 길어서 유효하지 않습니다. 제목의 최대 길이는 50자이며, 이 제목은 그 제한을 초과합니다.', 이것은 유효한 내용입니다."
	})
	@DisplayName("유효하지 않은 제목으로 게시물 생성 시 INVALID_TITLE_LENGTH 예외가 발생하는 것을 확인한다.")
	void postCreationWithInvalidTitleShouldThrowException(String title, String content) {
		// given
		PostCreateRequest request = new PostCreateRequest(title, content, 1L, null);
		PostCategory category = PostCategory.builder().title("카테고리").build();

		// Mocking the Member object
		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		// when, then
		assertThatThrownBy(() -> Post.of(request, category, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_TITLE_LENGTH);
	}

	@ParameterizedTest
	@CsvSource({
		"유효한 제목, '짧음'",
		"유효한 제목, ''"
	})
	@DisplayName("유효하지 않은 내용으로 게시물 생성 시 INVALID_CONTENT_LENGTH 예외가 발생하는 것을 확인한다.")
	void postCreationWithInvalidContentShouldThrowException(String title, String content) {
		// given
		PostCreateRequest request = new PostCreateRequest(title, content, 1L, null);
		PostCategory category = PostCategory.builder().title("카테고리").build();

		// Mocking the Member object
		Member member = mock(Member.class);
		when(member.getEmail()).thenReturn("test@mail.com");

		// when, then
		assertThatThrownBy(() -> Post.of(request, category, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_CONTENT_LENGTH);
	}
}
