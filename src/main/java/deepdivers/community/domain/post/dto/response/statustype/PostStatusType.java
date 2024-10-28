package deepdivers.community.domain.post.dto.response.statustype;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostStatusType implements StatusType {

	// 게시글 관련 상태
	POST_CREATE_SUCCESS(1200, "게시글 작성에 성공하였습니다."),
	POST_UPDATE_SUCCESS(1201, "게시글 수정에 성공하였습니다."),
	POST_DELETE_SUCCESS(1202, "게시글 삭제에 성공하였습니다."),
	POST_VIEW_SUCCESS(1203, "게시글 조회에 성공하였습니다."),
	POST_IMAGE_UPLOAD_SUCCESS(1204, "게시글 이미지 업로드에 성공하였습니다."),
	POST_LIKE_SUCCESS(1205, "게시글 좋아요에 성공하였습니다."),
	POST_UNLIKE_SUCCESS(1206, "게시글 좋아요 취소에 성공하였습니다.");

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
