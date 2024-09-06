package deepdivers.community.domain.member.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PhoneNumberTest {

    @Test
    @DisplayName("올바른 전화번호 입력 시 Contact 객체를 성공적으로 생성하는 것을 확인한다.")
    void fromWithValidPhoneNumberShouldCreateContact() {
        // given
        String validPhoneNumber = "010-1234-5678";
        // when
        PhoneNumber phoneNumber = new PhoneNumber(validPhoneNumber);
        // then
        assertThat(phoneNumber).isNotNull();
        assertThat(phoneNumber.getValue()).isEqualTo(validPhoneNumber);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "01012345678", "010-123-4567", "010-0000-0000", "000-0000-0000", "010-1234 -1234",
            "010-123 4-1234", " 010-1234-5678", "010-1234-5678 ", " 010-1234-5678 "
    })
    @DisplayName("올바르지 않은 전화번호 입력 시 유효하지 않은 전화번호 형식 예외가 떨어지는지 확인한다.")
    void fromWithInvalidPhoneNumberShouldThrowException(String invalidPhoneNumber) {
        // given, when, then
        assertThatThrownBy(() -> PhoneNumber.validator(invalidPhoneNumber))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.INVALID_PHONE_NUMBER_FORMAT);
    }

}