package deepdivers.community.domain.post.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.post.exception.PostExceptionCode;
import deepdivers.community.domain.common.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PostContentTest {

	@Test
	@DisplayName("올바른 내용을 입력 시 PostContent 객체를 성공적으로 생성하는 것을 확인한다.")
	void ofWithValidContentShouldCreatePostContent() {
		// given,
		String validContent = "content";

		// when
		PostContent postContent = PostContent.of(validContent);

		// then
		assertThat(postContent.getContent()).isEqualTo(validContent);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 빈_입력에_대해서_예외가_발생(String invalidContent) {
		// given, when, then
		assertThatThrownBy(() -> PostContent.of(invalidContent))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.VALUE_CANNOT_BE_NULL);
	}

	@Test
	void 빈_입력에_대해서_예외가_발생() {
		// given,
		String invalidContent = "1".repeat(4);

		// when, then
		assertThatThrownBy(() -> PostContent.of(invalidContent))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.INVALID_CONTENT_LENGTH);
	}
}
