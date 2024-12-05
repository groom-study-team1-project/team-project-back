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
@EqualsAndHashCode(of = "content")
public class PostContent {

	private static final int MIN_LENGTH = 5;

	@Column(name = "post_content", nullable = false, columnDefinition = "TEXT")
	private String content;

	public static PostContent of(final String content) {
		validateContent(content);
		return new PostContent(content);
	}

	public static void validateContent(final String content) {
		validateContentNotNull(content);
		validateContentLength(content);
	}

	private static void validateContentNotNull(final String content) {
		if (content == null) {
			throw new IllegalArgumentException("Content cannot be null");
		}
	}

	private static void validateContentLength(final String content) {
		if (content.length() < MIN_LENGTH) {
			throw new BadRequestException(PostExceptionType.INVALID_CONTENT_LENGTH);
		}
	}

}
