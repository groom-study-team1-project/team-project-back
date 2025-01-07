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
        Nickname nickname = new Nickname(validNickname);
        // then
        assertThat(nickname).isNotNull();
        assertThat(nickname.getValue()).isEqualTo(validNickname);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "일", "스물하나스물하나스물하나스물하나스물하나스물하나스"})
    @DisplayName("닉네임 길이에 대해 검증이 실패하는 경우 유효하지 닉네임 길이의 예외가 떨어지는 것을 확인한다.")
    void fromWithInvalidNicknameLengthShouldThrowException(String invalidNickname) {
        // given, when, then
        assertThatThrownBy(() -> new Nickname(invalidNickname))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.INVALID_NICKNAME_LENGTH);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "공 백", "1숫자로시작"})
    @DisplayName("닉네임 패턴에 대해 검증이 실패하는 경우 유효하지 않은 닉네임 형식의 예외가 떨어지는 것을 확인한다.")
    void fromWithInvalidNicknamePatternShouldThrowException(String invalidNickname) {
        // given, when, then
        assertThatThrownBy(() -> new Nickname(invalidNickname))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.INVALID_NICKNAME_FORMAT);
    }

    @Test
    @DisplayName("닉네임 객체의 동등성을 확인한다.")
    void equalNicknamesShouldBeEqual() {
        // given, when
        Nickname baseNickname = new Nickname("닉네임");
        Nickname compareNickname = new Nickname("닉네임");
        // then
        assertThat(baseNickname.equals(compareNickname)).isTrue();
    }

    @Test
    @DisplayName("닉네임 객체의 대소문자가 같다면 동등하다.")
    void nicknamesWithDifferentCasesShouldBeNotEqual() {
        // given, when
        Nickname baseNickname = new Nickname("nickName");
        Nickname compareNickname = new Nickname("nickname");

        // then
        assertThat(baseNickname.equals(compareNickname)).isTrue();
    }

}
