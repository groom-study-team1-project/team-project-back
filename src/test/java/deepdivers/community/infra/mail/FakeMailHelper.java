package deepdivers.community.infra.mail;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class FakeMailHelper implements MailHelper {

    private final Map<String, Data> db = new HashMap<>();
    private static final Duration CODE_EXPIRATION_TIME = Duration.ofMinutes(5);

    private static class Data {
        private final String verifyCode;
        private final LocalDateTime time;

        public Data(String verifyCode, LocalDateTime time) {
            this.verifyCode = verifyCode;
            this.time = time;
        }
    }

    @Override
    public void sendAuthenticatedEmail(final String email) {
        final String verifyCode = "123456";
        final LocalDateTime time = LocalDateTime.now();
        db.remove(email);
        db.put(email, new Data(verifyCode, time));
    }

    @Override
    public void verifyEmail(final String email, final String code) {
        final String verifyCode = getVerifyCode(email);
        validateVerifyCode(verifyCode, code);
        db.remove(email);
    }

    private String getVerifyCode(final String email) {
        final Data data = db.get(email);
        if (data != null && LocalDateTime.now().isBefore(data.time.plus(CODE_EXPIRATION_TIME))) {
            return data.verifyCode;
        }
        return null;
    }

}
