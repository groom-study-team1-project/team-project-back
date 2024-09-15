package deepdivers.community.domain.member.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.encryptor.Encryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PasswordTest {

    @Mock
    private Encryptor encryptor;

    @Test
    @DisplayName("패스워드 생성을 확인한다.")
    void passwordShouldBeCreate() {
        // given
        String passwordValue = "testPassword1!";

        // when
        Password encryptedPassword = new Password(encryptor, passwordValue);

        // then
        assertThat(encryptedPassword).isNotNull();
    }

    @Test
    @DisplayName("패스워드 생성 시 암호화를 확인한다.")
    void passwordShouldBeCorrectlyEncrypted() {
        // given
        String passwordValue = "testPassword1!";
        String encryptedPasswordValue = "encryptedPassword";
        when(encryptor.encrypt(passwordValue)).thenReturn(encryptedPasswordValue);

        // when
        Password encryptedPassword = new Password(encryptor, passwordValue);

        // then
        assertThat(encryptedPassword.getValue()).isEqualTo(encryptedPasswordValue);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1234", "test", "1 2 3 4 5 6 !", "password1 !"})
    @DisplayName("패스워드 생성 중 유효성 검증에 통과하지 못할 시 예외 발생을 확인한다.")
    void fromWithInvalidPasswordShouldThrowException(String invalidPassword) {
        // given, when, then
        assertThatThrownBy(() -> new Password(encryptor, invalidPassword))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.INVALID_PASSWORD_FORMAT);
    }

    @ParameterizedTest
    @ValueSource(strings = {" password1!", "password1! ", " password1! "})
    @DisplayName("Password 정보 양쪽에 공백이 포함된 경우 예외를 반환하는지 확인한다.")
    void fromWithIfBothEndsContainsSpacesShouldCreateTrimmedPassword(String passwordWithSpace) {
        // given
        // when, then
        assertThatThrownBy(() -> new Password(encryptor, passwordWithSpace))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.INVALID_PASSWORD_FORMAT);
    }

}