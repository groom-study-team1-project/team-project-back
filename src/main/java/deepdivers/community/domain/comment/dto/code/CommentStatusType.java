package deepdivers.community.domain.comment.dto.code;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentStatusType implements StatusType {

	COMMENT_CREATE_SUCCESS(1400, "댓글 작성에 성공하였습니다."),
	REPLY_CREATE_SUCCESS(1401, "답글 작성에 성공하였습니다."),
	COMMENT_EDIT_SUCCESS(1402, "댓글 수정에 성공하였습니다."),
	COMMENT_REMOVE_SUCCESS(1403, "댓글 삭제에 성공하였습니다."),
	COMMENT_GET_SUCCESS(1404, "댓글 조회에 성공하였습니다."),
	REPLY_GET_SUCCESS(1405, "답글 조회에 성공하였습니다."),
	;

	private final int code;
	private final String message;

}
