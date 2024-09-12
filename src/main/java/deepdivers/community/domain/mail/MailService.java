package deepdivers.community.domain.mail;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.mail.dto.AuthenticateEmailRequest;
import deepdivers.community.domain.mail.dto.statustype.EmailStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final String EMAIL_AUTH_SUBJECT = "[구름커뮤니티] 이메일 인증 메일입니다.";
    private static final Duration CODE_EXPIRATION_TIME = Duration.ofMinutes(5);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final RedisTemplate<String, String> redisTemplate;

    public NoContent sendAuthenticatedEmail(final AuthenticateEmailRequest request) {
        final String authCode = generateAuthCode();
        sendEmail(request.email(), authCode);

        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(request.email(), authCode, CODE_EXPIRATION_TIME);

        return NoContent.from(EmailStatus.SUCCESS);
    }

    private String generateAuthCode() {
        final int maxRange = 1000000;
        final Random random = new Random();
        return String.format("%06d", random.nextInt(maxRange));
    }

    private void sendEmail(String to, String authCode) {
        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(EMAIL_AUTH_SUBJECT);
            helper.setText(getEmailContent(authCode), true);
            mailSender.send(message);
        } catch (final MessagingException e) {
            // todo exception
            throw new IllegalStateException("email sender error: {}", e);
        }
    }

    private String getEmailContent(String authCode) {
        final Context context = new Context();
        context.setVariable("authCode", authCode);
        return templateEngine.process("auth-email", context);
    }

}
