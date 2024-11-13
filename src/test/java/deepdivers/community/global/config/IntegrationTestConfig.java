package deepdivers.community.global.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class IntegrationTestConfig {

    @Bean
    @Primary
    public S3Client s3Client() {
        return TestContainerInitializer.createS3Client();
    }

}