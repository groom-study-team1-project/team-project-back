package deepdivers.community.domain.like.dto.code;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LikeStatusType implements StatusType {

    POST_LIKE_SUCCESS(1205, "게시글 좋아요에 성공하였습니다."),
    POST_UNLIKE_SUCCESS(1206, "게시글 좋아요 취소에 성공하였습니다."),
    COMMENT_LIKE_SUCCESS(1406, "댓글 좋아요에 성공했습니다."),
    COMMENT_UNLIKE_SUCCESS(1407, "댓글 좋아요 취소에 성공했습니다.");
    ;

    private final int code;
    private final String message;

}
