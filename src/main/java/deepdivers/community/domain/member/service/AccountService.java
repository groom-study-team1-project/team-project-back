package deepdivers.community.domain.member.service;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.dto.request.VerifyEmailRequest;
import deepdivers.community.domain.member.dto.response.statustype.AccountStatusType;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final RedisTemplate<String, String> redisTemplate;

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

}
