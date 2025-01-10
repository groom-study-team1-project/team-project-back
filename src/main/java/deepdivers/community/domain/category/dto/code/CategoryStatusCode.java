package deepdivers.community.domain.category.dto.code;

import deepdivers.community.domain.common.dto.code.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryStatusCode implements StatusCode {

	// 카테고리 관련 상태
	CATEGORY_CREATE_SUCCESS(1300, "카테고리 생성에 성공하였습니다."),
	CATEGORY_VIEW_SUCCESS(1301, "카테고리 조회에 성공하였습니다."),
	CATEGORY_UPDATE_SUCCESS(1302, "카테고리 수정에 성공하였습니다."),
	CATEGORY_DELETE_SUCCESS(1303, "카테고리 삭제에 성공하였습니다."),
	POST_COUNT_BY_CATEGORY_VIEW_SUCCESS(1304, "카테고리 별 사용자 게시글 수 조회에 성공했습니다.");

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
