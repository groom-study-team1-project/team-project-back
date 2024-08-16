package deepdivers.community.utility.encryptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class IpEncryptor implements Encryptor {

    private static final String SALT = "abcdefghijklmnopqrstuvwxyz";

    public String encrypt(String ip) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final String saltedIp = ip + SALT;
            byte[] encodedHash = digest.digest(saltedIp.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }

    public Boolean matches(String rawIp, String encryptedIp) {
        return encrypt(rawIp).equals(encryptedIp);
    }
}
