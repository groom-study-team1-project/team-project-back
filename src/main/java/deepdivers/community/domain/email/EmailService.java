package deepdivers.community.domain.email;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.email.dto.VerifyEmailRequest;
import deepdivers.community.domain.email.dto.statustype.EmailStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String EMAIL_AUTH_SUBJECT = "[구름커뮤니티] 이메일 인증 메일입니다.";

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public NoContent sendAuthenticatedEmail(final VerifyEmailRequest request) {
        final String authCode = generateAuthCode();
        sendEmail(request.email(), EMAIL_AUTH_SUBJECT, authCode);
        // todo 생성된 인증 코드를 저장하는 로직 추가 (예: Redis)
        return NoContent.from(EmailStatus.SUCCESS);
    }

    private String generateAuthCode() {
        final int maxRange = 1000000;
        final Random random = new Random();
        return String.format("%06d", random.nextInt(maxRange));
    }

    private void sendEmail(String to, String subject, String authCode) {
        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(getEmailContent(authCode), true);
            mailSender.send(message);
        } catch (final MessagingException e) {
            // todo exception
            throw new IllegalStateException(e);
        }
    }

    private String getEmailContent(String authCode) {
        final Context context = new Context();
        context.setVariable("authCode", authCode);
        return templateEngine.process("auth-email", context);
    }

}
