package deepdivers.community.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.member.dto.request.AuthenticateEmailRequest;
import deepdivers.community.domain.member.dto.request.VerifyEmailRequest;
import deepdivers.community.domain.member.dto.response.statustype.AccountStatusType;
import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.mail.MailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(LocalStackTestConfig.class)
@Transactional
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    /*
     * 닉네임 검증 테스트
     * */
    @Test
    @DisplayName("올바른 닉네임으로 검사할 경우를 테스트한다.")
    void verifyNicknameSuccessTest() {
        // Given test.sql
        String nickname = "noDuplicate";

        // When
        NoContent noContent = accountService.verifyNickname(nickname);

        // then
        StatusResponse expectedStatus = StatusResponse.from(AccountStatusType.NICKNAME_VALIDATE_SUCCESS);
        StatusResponse resultStatus = noContent.status();
        assertThat(resultStatus.code()).isEqualTo(expectedStatus.code());
        assertThat(resultStatus.message()).isEqualTo(expectedStatus.message());
    }

    /*
     * 이메일 인증코드 전송 테스트
     * */
    @Test
    @DisplayName("올바른 이메일로 인증 코드를 전송할 경우를 테스트한다.")
    void sendAuthenticateEmailSuccessTest() {
        // Given test.sql
        AuthenticateEmailRequest request = new AuthenticateEmailRequest("email@test.com");

        // When
        NoContent noContent = accountService.emailAuthentication(request);

        // then
        StatusResponse expectedStatus = StatusResponse.from(AccountStatusType.SEND_VERIFY_CODE_SUCCESS);
        StatusResponse resultStatus = noContent.status();
        assertThat(resultStatus.code()).isEqualTo(expectedStatus.code());
        assertThat(resultStatus.message()).isEqualTo(expectedStatus.message());
    }

    /*
     * 이메일 인증코드 검사 테스트
     * */
    @Test
    @DisplayName("올바른 이메일과 인증 코드로 검증할 경우 성공한다.")
    void verifyEmailWithAuthenticateSuccessTest() {
        // Given, test.sql
        accountService.emailAuthentication(new AuthenticateEmailRequest("email@test.com"));
        VerifyEmailRequest request = new VerifyEmailRequest("email@test.com", "123456");

        // When
        NoContent noContent = accountService.verifyEmail(request);

        // then
        StatusResponse expectedStatus = StatusResponse.from(AccountStatusType.VERIFY_EMAIL_SUCCESS);
        StatusResponse resultStatus = noContent.status();
        assertThat(resultStatus.code()).isEqualTo(expectedStatus.code());
        assertThat(resultStatus.message()).isEqualTo(expectedStatus.message());
    }

    @Test
    @DisplayName("이메일로 인증 코드를 전송한 기록이 없을 경우 검증 시 예외가 발생한다.")
    void verifyNotSentEmailWithAuthenticateCodeShouldBeError() {
        // Given, test.sql
        VerifyEmailRequest request = new VerifyEmailRequest("noSentEmail@test.com", "123456");

        // when, then
        assertThatThrownBy(() -> accountService.verifyEmail(request))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", MailException.INVALID_VERIFY_CODE);
    }

    @Test
    @DisplayName("검증이 성공한 코드로 재검증 시 예외가 발생한다.")
    void twiceVerifyEmailWithAuthenticateCodeShouldBeError() {
        // Given, test.sql
        accountService.emailAuthentication(new AuthenticateEmailRequest("email@test.com"));
        VerifyEmailRequest request = new VerifyEmailRequest("email@test.com", "123456");
        accountService.verifyEmail(request);

        // when, then
        assertThatThrownBy(() -> accountService.verifyEmail(request))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", MailException.INVALID_VERIFY_CODE);
    }

    @Test
    @DisplayName("틀린 코드로 검증 시 예외가 발생한다.")
    void verifyEmailWithInvalidAuthenticateCodeShouldBeError() {
        // Given, test.sql
        accountService.emailAuthentication(new AuthenticateEmailRequest("email@test.com"));
        VerifyEmailRequest request = new VerifyEmailRequest("email@test.com", "111111");

        // when, then
        assertThatThrownBy(() -> accountService.verifyEmail(request))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", MailException.INVALID_VERIFY_CODE);
    }

}
