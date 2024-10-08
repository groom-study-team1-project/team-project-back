package deepdivers.community.domain.member.dto.response.statustype;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatusType implements StatusType {

    MEMBER_SIGN_UP_SUCCESS(1000, "사용자 회원가입에 성공하였습니다."),
    MEMBER_LOGIN_SUCCESS(1001, "사용자 로그인에 성공하였습니다."),
    GET_PROFILE_SUCCESS(1002, "프로필 조회에 성공하였습니다."),
    UPLOAD_IMAGE_SUCCESS(1004, "사용자 프로필 이미지 업로드에 성공하였습니다."),
    EMAIL_VALIDATE_SUCCESS(1006, "사용할 수 있는 이메일입니다."),
    UPDATE_PROFILE_SUCCESS(1007, "프로필 수정이 성공하였습니다."),
    UPDATE_PASSWORD_SUCCESS(1008, "비밀번호 변경이 성공하였습니다."),
    GET_MY_POSTS_SUCCESS(1009, "내가 쓴 게시글 조회에 성공하였습니다.");

    private final int code;
    private final String message;

}
