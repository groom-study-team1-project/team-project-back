package deepdivers.community.global.utility.encryptor;

public interface PasswordEncryptor {

    String encrypt(String plainText);
    Boolean matches(String plainText, String encodedText);

}
