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
	private static final int MAX_LENGTH = 100;  // 최대 길이 추가

	@Column(name = "post_content", nullable = false)
	private String content;  // 필드 이름을 "content"로 변경하여 구체화

	public static void validator(final String content) {
		validateContentLength(content);
	}

	private static void validateContentLength(final String content) {
		if (content.length() < MIN_LENGTH || content.length() > MAX_LENGTH) {
			throw new BadRequestException(PostExceptionType.INVALID_CONTENT_LENGTH);
		}
	}

	public static PostContent of(final String content) {
		validator(content);
		return new PostContent(content);
	}

}
