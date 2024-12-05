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
	private String title;

	public static PostTitle of(final String title) {
		validateTitle(title);
		return new PostTitle(title);
	}

	public static void validateTitle(final String title) {
		validateTitleNotNull(title);
		validateTitleLength(title);
	}

	private static void validateTitleNotNull(final String title) {
		if (title == null) {
			throw new BadRequestException(PostExceptionType.VALUE_CANNOT_BE_NULL);
		}
	}

	private static void validateTitleLength(final String title) {
		if (title.length() < MIN_LENGTH || title.length() > MAX_LENGTH) {
			throw new BadRequestException(PostExceptionType.INVALID_TITLE_LENGTH);
		}
	}

}
