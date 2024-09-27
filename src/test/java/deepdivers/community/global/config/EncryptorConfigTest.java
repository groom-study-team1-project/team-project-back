package deepdivers.community.global.config;

import static org.junit.jupiter.api.Assertions.*;

import deepdivers.community.domain.global.config.EncryptorConfig;
import deepdivers.community.domain.global.utility.encryptor.Encryptor;
import deepdivers.community.domain.global.utility.encryptor.EncryptorBean;
import deepdivers.community.domain.global.utility.encryptor.EncryptorTypes;
import deepdivers.community.domain.global.utility.encryptor.IpEncryptor;
import deepdivers.community.domain.global.utility.encryptor.PasswordEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextConfiguration(classes = {EncryptorConfig.class})
public class EncryptorConfigTest {

    @Autowired
    @EncryptorBean
    private Encryptor defaultEncryptor;

    @Autowired
    @EncryptorBean(EncryptorTypes.PASSWORD)
    private Encryptor passwordEncryptor;

    @Autowired
    @EncryptorBean(EncryptorTypes.IP)
    private Encryptor ipEncryptor;

    @Test
    @DisplayName("Encryptor 빈 DI 테스트")
    public void encryptorBeanDITest() {
        assertNotNull(defaultEncryptor);
        assertNotNull(passwordEncryptor);
        assertNotNull(ipEncryptor);
    }
    @Test
    @DisplayName("Encryptor 빈 Class 일치 테스트")
    public void encryptorBeanClassTest() {
        assertTrue(defaultEncryptor instanceof PasswordEncryptor);
        assertTrue(passwordEncryptor instanceof PasswordEncryptor);
        assertTrue(ipEncryptor instanceof IpEncryptor);
    }

}
