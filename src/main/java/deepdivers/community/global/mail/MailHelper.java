package deepdivers.community.global.mail;

import deepdivers.community.global.exception.model.BadRequestException;

public interface MailHelper {

    void sendAuthenticatedEmail(String email);
    void verifyEmail(String email, String code);

    default void validateVerifyCode(String verifyCode, String code) {
        if (verifyCode == null || verifyCode.isEmpty()) {
            throw new BadRequestException(MailException.NOT_SENT_VERIFY_CODE);
        }
        if (!verifyCode.equals(code)) {
            throw new BadRequestException(MailException.INVALID_VERIFY_CODE);
        }
    }

}
