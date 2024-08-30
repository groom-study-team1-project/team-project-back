package deepdivers.community.utility.uploader;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;


@Testcontainers
class S3ImageUploaderIntTest {

    @Container
    public static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                    .withServices(S3);

    private S3ImageUploader s3ImageUploader;
    private S3Client s3Client;

    @BeforeEach
    void setUp() throws Exception {
        s3Client = S3Client.builder()
                .endpointOverride(localStack.getEndpointOverride(S3))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        localStack.getAccessKey(), localStack.getSecretKey()
                )))
                .region(Region.of(localStack.getRegion()))
                .build();

        s3Client.createBucket(b -> b.bucket("test"));

        s3ImageUploader = new S3ImageUploader("test", s3Client);
    }

    @Test
    void uploadSuccessfully() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes()
        );
        Long memberId = 1L;

        // When
        String result = s3ImageUploader.upload(file, memberId);

        // Then
        assertTrue(result.contains("test"));
        assertTrue(result.contains("profiles/1/"));
        assertTrue(result.endsWith(".jpg"));
    }

}