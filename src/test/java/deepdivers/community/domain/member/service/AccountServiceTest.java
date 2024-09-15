package deepdivers.community.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.member.dto.request.AuthenticateEmailRequest;
import deepdivers.community.domain.member.dto.response.statustype.AccountStatusType;
import deepdivers.community.domain.member.exception.AccountException;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.mail.MailHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private MailHelper mailHelper;

    /*
    * 이메일 인증코드 전송 테스트
    * */
    @Test
    @DisplayName("올바른 이메일로 인증 코드를 전송할 경우를 테스트한다.")
    void sendAuthenticateEmailSuccessTest() {
        // Given test.sql
        AuthenticateEmailRequest request = new AuthenticateEmailRequest("email@test.com");

        // When
        NoContent noContent = accountService.sendAuthenticatedEmail(request);

        // then
        StatusResponse expectedStatus = StatusResponse.from(AccountStatusType.SEND_VERIFY_CODE_SUCCESS);
        StatusResponse resultStatus = noContent.status();
        assertThat(resultStatus.code()).isEqualTo(expectedStatus.code());
        assertThat(resultStatus.message()).isEqualTo(expectedStatus.message());
    }

}