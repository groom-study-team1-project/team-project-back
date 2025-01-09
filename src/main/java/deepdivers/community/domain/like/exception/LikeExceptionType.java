package deepdivers.community.domain.like.exception;

import deepdivers.community.global.exception.model.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikeExceptionType implements ExceptionType {

	INVALID_ACCESS(2500, "유효하지 않은 접근입니다.");

	private final int code;
	private final String message;
}

