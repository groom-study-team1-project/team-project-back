package deepdivers.community.global.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Profile("private")
@Component
@RequiredArgsConstructor
public class ProductMailHelper implements MailHelper {

    private static final String EMAIL_AUTH_SUBJECT = "[구름커뮤니티] 이메일 인증 메일입니다.";
    private static final Duration CODE_EXPIRATION_TIME = Duration.ofMinutes(5);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final RedisTemplate<String, String> redisTemplate;

    public void sendAuthenticatedEmail(final String email) {
        final String authCode = generateAuthCode();
        sendEmail(email, EMAIL_AUTH_SUBJECT, authCode);

        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(email, authCode, CODE_EXPIRATION_TIME);
    }

    private String generateAuthCode() {
        final int maxRange = 1000000;
        final Random random = new Random();
        return String.format("%06d", random.nextInt(maxRange));
    }

    private void sendEmail(final String to, final String subject, final String authCode) {
        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
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
