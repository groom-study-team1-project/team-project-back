package deepdivers.community.domain.post.dto.code;

import deepdivers.community.domain.common.dto.code.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostStatusCode implements StatusCode {

	POST_CREATE_SUCCESS(1200, "게시글 작성에 성공하였습니다."),
	POST_UPDATE_SUCCESS(1201, "게시글 수정에 성공하였습니다."),
	POST_DELETE_SUCCESS(1202, "게시글 삭제에 성공하였습니다."),
	POST_VIEW_SUCCESS(1203, "게시글 조회에 성공하였습니다."),
	POST_IMAGE_UPLOAD_SUCCESS(1204, "게시글 이미지 업로드에 성공하였습니다."),
	MY_POSTS_GETTING_SUCCESS(1009, "내가 쓴 게시글 조회에 성공하였습니다."),
	PROJECT_POST_CREATE_SUCCESS(1205, "프로젝트 게시글 작성에 성공했습니다."),
	PROJECT_POST_UPDATE_SUCCESS(1206, "프로젝트 게시글 수정에 성공했습니다."),
	PROJECT_POST_DELETE_SUCCESS(1207, "프로젝트 게시글 삭제에 성공했습니다."),
	;

	private final int code;
	private final String message;

}
