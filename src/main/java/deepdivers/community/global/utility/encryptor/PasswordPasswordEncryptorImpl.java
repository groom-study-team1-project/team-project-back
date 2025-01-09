package deepdivers.community.global.utility.encryptor;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordPasswordEncryptorImpl implements PasswordEncryptor {

    public String encrypt(final String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }

    public Boolean matches(final String plainText, final String encodedText) {
        return BCrypt.checkpw(plainText, encodedText);
    }

}
