package deepdivers.community.global.config;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class LocalStackTestConfig {

    private static final LocalStackContainer localStack;
    private static final S3Client s3Client;

    static {
        // LocalStack 컨테이너 시작
        localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(S3);
        localStack.start();

        // S3 클라이언트 생성
        s3Client = S3Client.builder()
            .endpointOverride(localStack.getEndpointOverride(S3))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
            ))
            .region(Region.of(localStack.getRegion()))
            .build();

        // 기본 버킷 생성
        s3Client.createBucket(b -> b.bucket("test-bucket"));
    }

    @Bean(destroyMethod = "")
    @Primary
    public S3Client s3Client() {
        return s3Client;
    }

}
