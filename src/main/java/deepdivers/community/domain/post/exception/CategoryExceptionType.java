package deepdivers.community.domain.post.exception;

import deepdivers.community.global.exception.model.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryExceptionType implements ExceptionType {

	CATEGORY_NOT_FOUND(3200, "해당 카테고리를 찾을 수 없습니다."),
	CATEGORY_ALREADY_EXISTS(3201, "이미 존재하는 카테고리입니다.");

	private final int code;
	private final String message;
}
