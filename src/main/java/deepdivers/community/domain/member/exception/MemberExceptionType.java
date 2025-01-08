package deepdivers.community.domain.member.exception;

import deepdivers.community.global.exception.model.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberExceptionType implements ExceptionType {

    INVALID_PASSWORD_FORMAT(2000, "사용자 비밀번호는 8글자부터 16글자로 영어 소문자, 특수문자, 숫자를 조합해주세요."),
    INVALID_NICKNAME_LENGTH(2001, "사용자 닉네임은 2글자부터 최대 20자입니다."),
    INVALID_NICKNAME_FORMAT(2002, "사용할 수 없는 닉네임입니다."),
    ALREADY_REGISTERED_EMAIL(2003, "이미 가입된 사용자 이메일입니다."),
    ALREADY_REGISTERED_NICKNAME(2004, "이미 가입된 사용자 닉네임입니다."),
    INVALID_PHONE_NUMBER_FORMAT(2005, "전화번호 형식을 맞춰주세요. ex) 010-0000-0000"),
    NOT_FOUND_ACCOUNT(2006, "이메일 계정 또는 비밀번호가 잘못되었습니다."),
    MEMBER_LOGIN_DORMANCY(2007, "휴면처리 된 계정입니다."),
    MEMBER_LOGIN_UNREGISTER(2008, "탈퇴처리가 진행중인 계정입니다."),
    NOT_FOUND_MEMBER(2009, "사용자 정보를 찾을 수 없습니다."),
    INVALID_EMAIL_FORMAT(2010, "이메일 형식을 맞춰주세요."),
    INVALID_PASSWORD(2011, "비밀번호가 틀렸습니다."),
    ALREADY_USING_PASSWORD(2012, "동일한 비밀번호로 변경할 수 없습니다.");

    private final int code;
    private final String message;

}
