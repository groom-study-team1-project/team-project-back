package deepdivers.community.global.config;

import deepdivers.community.global.utility.encryptor.PasswordEncryptor;
import deepdivers.community.global.utility.encryptor.PasswordPasswordEncryptorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EncryptorConfig {

    @Bean
    public PasswordEncryptor passwordEncryptor() {
        return new PasswordPasswordEncryptorImpl();
    }

}
