package deepdivers.community.domain.global.config;

import deepdivers.community.domain.global.utility.encryptor.Encryptor;
import deepdivers.community.domain.global.utility.encryptor.EncryptorBean;
import deepdivers.community.domain.global.utility.encryptor.EncryptorTypes;
import deepdivers.community.domain.global.utility.encryptor.IpEncryptor;
import deepdivers.community.domain.global.utility.encryptor.PasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EncryptorConfig {

    @Bean
    @Primary
    @EncryptorBean(EncryptorTypes.PASSWORD)
    public Encryptor passwordEncryptor() {
        return new PasswordEncryptor();
    }

    @Bean
    @EncryptorBean(EncryptorTypes.IP)
    public Encryptor ipEncryptor() {
        return new IpEncryptor();
    }

}
