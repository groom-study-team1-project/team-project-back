package deepdivers.community.domain.post.exception;

import deepdivers.community.global.exception.model.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostExceptionType implements ExceptionType {

	// 제목 및 내용 길이에 관한 상태
	INVALID_TITLE_LENGTH(2200, "게시글 제목은 2자 이상, 50자 이하로 작성해야 합니다."),
	INVALID_CONTENT_LENGTH(2201, "게시글 내용은 최소 5자 이상이어야 합니다."),
	POST_NOT_FOUND(2202, "해당 게시글을 찾을 수 없습니다."),
	NOT_POST_AUTHOR(2203, "게시글 작성자가 아닙니다."),
	INVALID_IMAGE_FILE(2204, "유효하지 않은 이미지 파일입니다.");

	private final int code;
	private final String message;
}

