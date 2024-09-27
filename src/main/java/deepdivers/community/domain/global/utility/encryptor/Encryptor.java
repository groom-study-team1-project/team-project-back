package deepdivers.community.domain.global.utility.encryptor;

public interface Encryptor {

    String encrypt(String plainText);
    Boolean matches(String plainText, String encodedText);

}
