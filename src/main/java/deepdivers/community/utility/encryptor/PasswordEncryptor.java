package deepdivers.community.utility.encryptor;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncryptor implements Encryptor {

    public String encrypt(final String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }

    public Boolean matches(final String plainText, final String encodedText) {
        return BCrypt.checkpw(plainText, encodedText);
    }

}
