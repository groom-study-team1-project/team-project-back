package deepdivers.community.domain.global.config.localstack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Configuration
@Profile("local")
public class LocalStackConfig {

    private static final DockerImageName DOCKER_IMAGE = DockerImageName.parse("localstack/localstack:latest");
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public LocalStackContainer localStackContainer() {
        final LocalStackContainer container = new SimpleLocalStackContainer(DOCKER_IMAGE);
        container.withServices(Service.S3);

        return container;
    }

    @Bean
    public S3Client amazonS3Client(final LocalStackContainer localStackContainer) {
        final S3Client s3Client = S3Client.builder()
                .endpointOverride(localStackContainer.getEndpointOverride(Service.S3))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        localStackContainer.getAccessKey(), localStackContainer.getSecretKey()
                )))
                .region(Region.AP_NORTHEAST_2)
                .build();

        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        return s3Client;
    }

}
