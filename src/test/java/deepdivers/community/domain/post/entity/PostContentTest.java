package deepdivers.community.domain.post.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PostContentTest {

	@ParameterizedTest
	@ValueSource(strings = {"This is valid content.", "1234567890", "This content is exactly ten characters."})
	@DisplayName("올바른 내용을 입력 시 PostContent 객체를 성공적으로 생성하는 것을 확인한다.")
	void ofWithValidContentShouldCreatePostContent(String validContent) {
		// given, when
		PostContent postContent = PostContent.of(validContent);
		// then
		assertThat(postContent).isNotNull();
		assertThat(postContent.getContent()).isEqualTo(validContent);
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "1234"})
	@DisplayName("내용 길이에 대해 검증이 실패하는 경우 유효하지 않은 내용 길이의 예외가 떨어지는 것을 확인한다.")
	void ofWithInvalidContentLengthShouldThrowException(String invalidContent) {
		// given, when, then
		assertThatThrownBy(() -> PostContent.of(invalidContent))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_CONTENT_LENGTH);
	}
}
