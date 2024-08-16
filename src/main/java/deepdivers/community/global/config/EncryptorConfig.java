package deepdivers.community.global.config;

import deepdivers.community.utility.encryptor.Encryptor;
import deepdivers.community.utility.encryptor.EncryptorBean;
import deepdivers.community.utility.encryptor.EncryptorTypes;
import deepdivers.community.utility.encryptor.IpEncryptor;
import deepdivers.community.utility.encryptor.PasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EncryptorConfig {

    @Bean
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
