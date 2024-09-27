package deepdivers.community.domain.post.dto.response.statustype;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikeStatusType implements StatusType {

	COMMENT_LIKE_SUCCESS(1500, "댓글 좋아요에 성공하였습니다.");

	private final int code;
	private final String message;

}
