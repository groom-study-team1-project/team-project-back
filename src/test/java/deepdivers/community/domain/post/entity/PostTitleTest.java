package deepdivers.community.domain.post.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class PostTitleTest {

	@Test
	@DisplayName("올바른 제목 입력 시 PostTitle 객체를 성공적으로 생성하는 것을 확인한다.")
	void ofWithValidTitleShouldCreatePostTitle() {
		// given
		// when
		PostTitle postTitle = PostTitle.of("제목입니당");
		// then
		assertThat(postTitle.getTitle()).isEqualTo("제목입니당");
	}

	@ParameterizedTest
	@NullAndEmptySource
	void ofWithInvalidTitleLengthShouldThrowException(String invalidTitle) {
		// given, when, then
		assertThatThrownBy(() -> PostTitle.of(invalidTitle))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.VALUE_CANNOT_BE_NULL);
	}

	@Test
	void 제목_최소_경계값_테스트() {
		// given
		String title = "1";

		// when, then
		assertThatThrownBy(() -> PostTitle.of(title))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.INVALID_TITLE_LENGTH);
	}

	@Test
	void 제목_최대_경계값_테스트() {
		// given
		String title = "1".repeat(51);

		// when, then
		assertThatThrownBy(() -> PostTitle.of(title))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.INVALID_TITLE_LENGTH);
	}

}
