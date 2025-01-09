package deepdivers.community.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.member.exception.MemberExceptionCode;
import deepdivers.community.domain.common.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailTest {

    @Test
    @DisplayName("이메일 생성을 확인한다.")
    void emailShouldBeCreate() {
        // given
        String emailValue = "email1@mail.com";

        // when
        Email email = new Email(emailValue);

        // then
        assertThat(email.getValue()).isEqualTo(emailValue);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        " firstspace@mail.com", "lastspace@mail.com ", " bothspace@mail.com ", "nomail",
        "한글메일@mail.com", "korean@메일.com", "space @mail.com"
    })
    @DisplayName("유효하지 않은 이메일 생성 시 예외가 발생한다.")
    void invalidEmailFormatTest(String email) {
        // given & When & Then
        assertThatThrownBy(() -> new Email(email))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionCode.INVALID_EMAIL_FORMAT);
    }
}
