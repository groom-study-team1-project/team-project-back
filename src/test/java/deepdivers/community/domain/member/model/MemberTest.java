package deepdivers.community.domain.member.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.utility.encryptor.Encryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberTest {

    @Mock
    private Encryptor encryptor;

    @Test
    @DisplayName("유효한 사용자 계정 생성을 확인한다.")
    void accountSignUpShouldCreateValidAccount() {
        // given
        String nickname = "닉네임";
        String imageUrl = "img";
        String phoneNumber = "010-1234-5678";
        String email = "test@mail.com";
        String password = "password1!";
        String encryptedPassword = "encryptedPasswordValue";
        MemberSignUpRequest request = new MemberSignUpRequest(email, password, nickname, imageUrl, phoneNumber);

        when(encryptor.encrypt(password)).thenReturn(encryptedPassword);

        // when
        Member member = Member.of(request, encryptor);

        // then
        assertThat(member).isNotNull();
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getPassword()).isEqualTo(encryptedPassword);
    }

    @ParameterizedTest
    @CsvSource({
            "닉네임, img, 010-1234-5678, email, 123!",
            "닉네임, img, 010-1234-5678, email, test!",
            "닉네임, img, 010-1234-5678, email, test1!",
            "닉네임, img, 010-1234-5678, email, test12345 !",
    })
    @DisplayName("유효하지 않은 사용자 계정일 경우 예외를 확인한다.")
    void accountSignUpShouldInValidAccountException(
            String nickname, String imageUrl, String phoneNumber, String email, String password
    ) {
        // given
        MemberSignUpRequest request = new MemberSignUpRequest(email, password, nickname, imageUrl, phoneNumber);
        // when, then
        assertThatThrownBy(() -> Member.of(request, encryptor))
                .isInstanceOf(BadRequestException.class);
    }

}