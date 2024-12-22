package deepdivers.community.global.config;

import deepdivers.community.infra.mail.FakeMailHelper;
import deepdivers.community.infra.mail.MailHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestMailConfig {

    @Bean
    @Primary
    public MailHelper mailHelper() {
        return new FakeMailHelper();
    }

}
