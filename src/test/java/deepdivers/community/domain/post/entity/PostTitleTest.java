package deepdivers.community.domain.post.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PostTitleTest {

	@ParameterizedTest
	@ValueSource(strings = {"Valid Title", "Short Title", "This is a post title"})
	@DisplayName("올바른 제목 입력 시 PostTitle 객체를 성공적으로 생성하는 것을 확인한다.")
	void ofWithValidTitleShouldCreatePostTitle(String validTitle) {
		// given, when
		PostTitle postTitle = PostTitle.of(validTitle);
		// then
		assertThat(postTitle).isNotNull();
		assertThat(postTitle.getTitle()).isEqualTo(validTitle);
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "1", "This title is way too long to be valid because it exceeds the maximum allowed character limit of one hundred characters."})
	@DisplayName("제목 길이에 대해 검증이 실패하는 경우 유효하지 않은 제목 길이의 예외가 떨어지는 것을 확인한다.")
	void ofWithInvalidTitleLengthShouldThrowException(String invalidTitle) {
		// given, when, then
		assertThatThrownBy(() -> PostTitle.of(invalidTitle))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.INVALID_TITLE_LENGTH);
	}
}
