package deepdivers.community.domain.post.exception;

import deepdivers.community.global.exception.model.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentExceptionType implements ExceptionType {

	NOT_FOUND_COMMENT(2200, "댓글 정보가 없습니다.");

	private final int code;
	private final String message;
}

