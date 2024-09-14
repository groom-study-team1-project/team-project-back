package deepdivers.community.global.mail;

public interface MailHelper {

    void sendAuthenticatedEmail(String email);
    void verifyEmail(String email, String code);

    default void validateVerifyCode(String verifyCode, String code) {
        if (verifyCode == null || verifyCode.isEmpty()) {
            throw new IllegalArgumentException("이메일 전송이 되지 않은 계정");
        }
        if (!verifyCode.equals(code)) {
            throw new IllegalArgumentException("잘못된 인증 코드");
        }
    }

}
