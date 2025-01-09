package deepdivers.community.domain.hashtag.exception;

import deepdivers.community.domain.common.dto.code.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HashtagExceptionCode implements ExceptionCode {

	INVALID_HASHTAG_FORMAT(3302, "유효하지 않은 해시태그 형식입니다.");

	private final int code;
	private final String message;
}
