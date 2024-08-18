package deepdivers.community.domain.member.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NicknameTest {

    @ParameterizedTest
    @ValueSource(strings = {"닉네임", "안녕하세요", "하나둘셋넷", "abcd", "hi안"})
    @DisplayName("올바른 닉네임 입력 시 NickName 객체를 성공적으로 생성하는 것을 확인한다.")
    void fromWithValidNicknameShouldCreateNickname(String validNickname) {
        // given, when
        Nickname nickname = Nickname.from(validNickname);
        // then
        assertThat(nickname).isNotNull();
        assertThat(nickname.getValue()).isEqualTo(validNickname);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "일", "스물하나스물하나스물하나스물하나스물하나스물하나스"})
    @DisplayName("닉네임 길이에 대해 검증이 실패하는 경우 유효하지 닉네임 길이의 예외가 떨어지는 것을 확인한다.")
    void fromWithInvalidNicknameLengthShouldThrowException(String invalidNickname) {
        // given, when, then
        assertThatThrownBy(() -> Nickname.from(invalidNickname))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.INVALID_NICKNAME_LENGTH);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "공 백", "1숫자로시작"})
    @DisplayName("닉네임 패턴에 대해 검증이 실패하는 경우 유효하지 않은 닉네임 형식의 예외가 떨어지는 것을 확인한다.")
    void fromWithInvalidNicknamePatternShouldThrowException(String invalidNickname) {
        // given, when, then
        assertThatThrownBy(() -> Nickname.from(invalidNickname))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.INVALID_NICKNAME_FORMAT);
    }

    @ParameterizedTest
    @ValueSource(strings = {" 맨앞에공백", "맨뒤에공백 ", " 양쪽에공백 "})
    @DisplayName("닉네임 양쪽에 공백이 포함된 경우 제거된 결과를 반환하는지 확인한다.")
    void fromWithIfBothEndsContainsSpacesShouldCreateTrimmedNickname(String spaceNickname) {
        // given
        String trimmedNickname = spaceNickname.trim();
        // when
        Nickname nickname = Nickname.from(spaceNickname);
        // then
        assertThat(nickname).isNotNull();
        assertThat(nickname.getValue()).isEqualTo(trimmedNickname);
    }

    @Test
    @DisplayName("닉네임 객체의 동등성을 확인한다.")
    void equalNicknamesShouldBeEqual() {
        // given, when
        Nickname baseNickname = Nickname.from("닉네임");
        Nickname compareNickname = Nickname.from("닉네임");
        // then
        assertThat(baseNickname.equals(compareNickname)).isTrue();
    }

    @Test
    @DisplayName("닉네임 객체의 대소문자 동등성을 확인한다.")
    void nicknamesWithDifferentCasesShouldBeEqual() {
        // given, when
        Nickname baseNickname = Nickname.from("nickName");
        Nickname compareNickname = Nickname.from("nickname");
        // then
        assertThat(baseNickname.equals(compareNickname)).isTrue();
    }

}