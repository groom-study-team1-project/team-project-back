package deepdivers.community.utility.encryptor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import deepdivers.community.global.config.EncryptorConfig;
import deepdivers.community.global.utility.encryptor.Encryptor;
import deepdivers.community.global.utility.encryptor.EncryptorBean;
import deepdivers.community.global.utility.encryptor.EncryptorTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextConfiguration(classes = {EncryptorConfig.class})
class EncryptorTest {

    private static final String IP = "192.168.0.1";
    private static final String DIFFERENT_IP = "192.168.0.2";
    private static final String PASSWORD = "testPassword";

    @Autowired
    @EncryptorBean
    private Encryptor passwordEncryptor;

    @Autowired
    @EncryptorBean(EncryptorTypes.IP)
    private Encryptor ipEncryptor;

    @Test
    @DisplayName("PASSWORD의 BCRYPT 알고리즘 암호화된 결과를 반환하는지 확인한다.")
    void passwordEncryptShouldReturnEncryptedString() {
        String encryptedPassword = passwordEncryptor.encrypt(PASSWORD);

        assertThat(encryptedPassword).isNotNull();
        assertThat(encryptedPassword).isNotEqualTo(PASSWORD);
    }

    @Test
    @DisplayName("BCRYPT 알고리즘이 같은 Password에 다른 암호화 결과를 반환하는지 확인한다.")
    void encryptShouldReturnDifferentValuesForSameInput() {
        String firstEncryption = passwordEncryptor.encrypt(PASSWORD);
        String secondEncryption = passwordEncryptor.encrypt(PASSWORD);

        assertThat(firstEncryption).isNotEqualTo(secondEncryption);
    }

    @Test
    @DisplayName("암호화된 PASSWORD와 평문 PASSWORD가 동일할 경우 True를 반환하는지 확인한다.")
    void matchesShouldReturnTrueForMatchingPasswordAndEncryptedPassword() {
        String encryptedPassword = passwordEncryptor.encrypt(PASSWORD);

        assertThat(passwordEncryptor.matches(PASSWORD, encryptedPassword)).isTrue();
    }

    @Test
    @DisplayName("암호화된 PASSWORD와 평문 PASSWORD가 동일하지 않을 경우 False를 반환하는지 확인한다.")
    void matchesShouldReturnFalseForNonMatchingPasswordAndEncryptedPassword() {
        String password2 = "differentPassword";
        String encryptedPassword1 = passwordEncryptor.encrypt(PASSWORD);

        assertThat(passwordEncryptor.matches(password2, encryptedPassword1)).isFalse();
    }

    @Test
    @DisplayName("IP의 SHA-256 알고리즘 암호화를 확인한다.")
    void ipEncryptShouldReturnEncryptedString() {
        String encryptedIp = ipEncryptor.encrypt(IP);

        assertThat(encryptedIp).isNotNull();
        assertThat(encryptedIp).isNotEqualTo(IP);
    }

    @Test
    @DisplayName("동일한 IP를 SHA-256 알고리즘 암호화 시 동일한 암호화가 되는지 확인한다.")
    void encryptShouldReturnSameValueForSameInput() {
        String firstEncryption = ipEncryptor.encrypt(IP);
        String secondEncryption = ipEncryptor.encrypt(IP);

        assertThat(firstEncryption).isEqualTo(secondEncryption);
    }

    @Test
    @DisplayName("평문 IP를 암호화 후 같은 평문 IP와 동일성을 확인한다.")
    void matchesShouldReturnTrueForMatchingIpAndEncryptedIp() {
        String encryptedIp = ipEncryptor.encrypt(IP);

        assertThat(ipEncryptor.matches(IP, encryptedIp)).isTrue();
    }

    @Test
    @DisplayName("평문 IP를 암호화 후 다른 평문 IP와 동일성을 확인한다.")
    void matches_shouldReturnFalseForNonMatchingIpAndEncryptedIp() {
        String encryptedIp = ipEncryptor.encrypt(IP);

        assertThat(ipEncryptor.matches(DIFFERENT_IP, encryptedIp)).isFalse();
    }

}