package deepdivers.community.global.mail;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@Slf4j
public class LocalMailHelper implements MailHelper {

    private final Map<String, String> db = new HashMap<>();

    public void sendAuthenticatedEmail(final String email) {
        final String verifyCode = "000000";
        db.put(email, verifyCode);
        log.info("============ send email ========> verifyCode = {}", verifyCode);
    }

}
