package deepdivers.community.domain.member.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import deepdivers.community.domain.member.dto.request.info.MemberAccountInfo;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.utility.encryptor.Encryptor;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountTest {

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

        Member member = new Member(nickname, imageUrl, phoneNumber);
        MemberAccountInfo accountInfo = new MemberAccountInfo(email, password);
        when(encryptor.encrypt(password)).thenReturn(encryptedPassword);

        // when
        Account account = Account.accountSignUp(accountInfo, encryptor, member);

        // then
        assertThat(account).isNotNull();
        assertThat(account.getEmail()).isEqualTo(email);
        assertThat(account.getPassword().getValue()).isEqualTo(encryptedPassword);
        assertThat(account.getMember()).usingRecursiveComparison().isEqualTo(member);
    }

    @ParameterizedTest
    @CsvSource({
            "닉네임, img, 010-1234-5678, email, 123!",
            "닉네임, img, 010-1234-5678, email, test!",
            "닉네임, img, 010-1234-5678, email, test1!",
            "닉네임, img, 010-1234-5678, email, test12345 !",
            "닉네임, img, 010-1234-5678, email,     ",
    })
    @DisplayName("유효하지 않은 사용자 계정일 경우 예외를 확인한다.")
    void accountSignUpShouldInValidAccountException(
            String nickname, String imageUrl, String phoneNumber, String email, String password
    ) {
        // given
        Member member = new Member(nickname, imageUrl, phoneNumber);
        MemberAccountInfo accountInfo = new MemberAccountInfo(email, password);

        // when, then
        assertThatThrownBy(() -> Account.accountSignUp(accountInfo, encryptor, member))
                .isInstanceOf(BadRequestException.class);
    }

}