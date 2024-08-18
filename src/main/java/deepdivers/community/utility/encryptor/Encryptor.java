package deepdivers.community.utility.encryptor;

public interface Encryptor {

    String encrypt(String plainText);
    Boolean matches(String plainText, String encodedText);

}
