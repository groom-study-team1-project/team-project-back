package deepdivers.community.domain.comment.exception;

import deepdivers.community.global.exception.model.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentExceptionType implements ExceptionType {

	NOT_FOUND_COMMENT(2300, "댓글 정보가 없습니다."),
	INVALID_COMMENT_CONTENT(2301, "댓글은 100자 이하로 작성해주세요."),
	INVALID_ACCESS(2302, "유효하지 않은 접근입니다.");

	private final int code;
	private final String message;
}

