package deepdivers.community.utility.encryptor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import deepdivers.community.global.config.EncryptorConfig;
import deepdivers.community.global.utility.encryptor.PasswordEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextConfiguration(classes = {EncryptorConfig.class})
class PasswordEncryptorTest {

    private static final String PASSWORD = "testPassword";

    @Autowired
    private PasswordEncryptor passwordEncryptor;

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

}
