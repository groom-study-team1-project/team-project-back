package deepdivers.community.global.config;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class TestContainerInitializer {

    private static final LocalStackContainer localStack;

    static {
        localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(S3);
        localStack.start();
    }

    public static LocalStackContainer getLocalStack() {
        return localStack;
    }

    public static S3Client createS3Client() {
        return S3Client.builder()
            .endpointOverride(localStack.getEndpointOverride(S3))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
            ))
            .region(Region.of(localStack.getRegion()))
            .build();
    }

}