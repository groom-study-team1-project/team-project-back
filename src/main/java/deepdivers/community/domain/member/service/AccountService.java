package deepdivers.community.domain.member.service;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.global.mail.MailHelper;
import deepdivers.community.domain.member.dto.request.AuthenticateEmailRequest;
import deepdivers.community.domain.member.dto.request.VerifyEmailRequest;
import deepdivers.community.domain.member.dto.response.statustype.AccountStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MailHelper mailHelper;

    public NoContent verifyEmail(final VerifyEmailRequest request) {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        final String verifyCode = valueOperations.get(request.email());

        validateVerifyCode(verifyCode, request.verifyCode());

        valueOperations.getAndDelete(request.email());
        return NoContent.from(AccountStatusType.VERIFY_EMAIL_SUCCESS);
    }

    private void validateVerifyCode(final String verifyCode, final String clientInputCode) {
        if (verifyCode == null || verifyCode.isEmpty()) {
            throw new IllegalArgumentException("이메일 전송이 되지 않은 계정");
        }
        if (!verifyCode.equals(clientInputCode)) {
            throw new IllegalArgumentException("잘못된 인증 코드");
        }
    }

    public NoContent sendAuthenticatedEmail(final AuthenticateEmailRequest request) {
        mailHelper.sendAuthenticatedEmail(request.email());
        return NoContent.from(AccountStatusType.SEND_VERIFY_CODE_SUCCESS);
    }

}
