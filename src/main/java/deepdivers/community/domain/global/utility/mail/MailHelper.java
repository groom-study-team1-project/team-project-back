package deepdivers.community.domain.global.utility.mail;

import deepdivers.community.domain.global.exception.model.BadRequestException;

public interface MailHelper {

    void sendAuthenticatedEmail(String email);
    void verifyEmail(String email, String code);

    default void validateVerifyCode(String verifyCode, String code) {
        if (verifyCode == null || verifyCode.isEmpty() || !verifyCode.equals(code)) {
            throw new BadRequestException(MailException.INVALID_VERIFY_CODE);
        }
    }

}
