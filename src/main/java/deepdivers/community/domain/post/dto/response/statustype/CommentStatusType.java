package deepdivers.community.domain.post.dto.response.statustype;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentStatusType implements StatusType {

	COMMENT_CREATE_SUCCESS(1300, "댓글 작성에 성공하였습니다."),
	REPLY_CREATE_SUCCESS(1301, "답글 작성에 성공하였습니다."),
	COMMENT_EDIT_SUCCESS(1302, "댓글 수정에 성공하였습니다."),
	COMMENT_REMOVE_SUCCESS(1303, "댓글 삭제에 성공하였습니다."),
	COMMENT_GET_SUCCESS(1304, "댓글 조회에 성공하였습니다."),
	REPLY_GET_SUCCESS(1305, "답글 조회에 성공하였습니다.");

	private final int code;
	private final String message;

}
