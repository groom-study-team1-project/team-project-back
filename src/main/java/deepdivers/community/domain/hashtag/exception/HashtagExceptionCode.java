package deepdivers.community.domain.hashtag.exception;

import deepdivers.community.domain.common.dto.code.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HashtagExceptionCode implements ExceptionCode {

	HASHTAG_NOT_FOUND(3300, "해당 해시태그를 찾을 수 없습니다."),
	HASHTAG_ALREADY_EXISTS(3301, "이미 존재하는 해시태그입니다."),
	INVALID_HASHTAG_FORMAT(3302, "유효하지 않은 해시태그 형식입니다.");

	private final int code;
	private final String message;
}
