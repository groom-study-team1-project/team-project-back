package deepdivers.community.domain.post.model;

import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "title")
public class PostTitle {

	private static final int MIN_LENGTH = 2;
	private static final int MAX_LENGTH = 50;

	@Column(name = "post_title", nullable = false, length = 50)
	private String title;  // 필드 이름을 "title"로 변경하여 구체화

	public static void validator(final String title) {
		validateTitleLength(title);
	}

	private static void validateTitleLength(final String title) {
		if (title.length() < MIN_LENGTH || title.length() > MAX_LENGTH) {
			throw new BadRequestException(PostExceptionType.INVALID_TITLE_LENGTH);
		}
	}

	public static PostTitle of(final String title) {
		validator(title);
		return new PostTitle(title);
	}
}
