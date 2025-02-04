package deepdivers.community.domain.category.exception;

import deepdivers.community.domain.common.dto.code.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryExceptionCode implements ExceptionCode {

	CATEGORY_NOT_FOUND(3200, "해당 카테고리를 찾을 수 없습니다."),
	CATEGORY_ALREADY_EXISTS(3201, "이미 존재하는 카테고리입니다."),
	INVALID_GENERAL_CATEGORY(3202, "범용 카테고리가 아닙니다."),
	INVALID_PROJECT_CATEGORY(3203, "프로젝트 카테고리가 아닙니다."),
    INVALID_CATEGORY_TYPE(3204, "카테고리 정보가 잘못되었습니다.");

	private final int code;
	private final String message;
}
