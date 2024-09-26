package deepdivers.community.domain.post.dto.response.statustype;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentStatusType implements StatusType {

	COMMENT_CREATE_SUCCESS(1300, "댓글 작성에 성공하였습니다."),
	REPLY_CREATE_SUCCESS(1301, "답글 작성에 성공하였습니다.");

	private final int code;
	private final String message;

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
