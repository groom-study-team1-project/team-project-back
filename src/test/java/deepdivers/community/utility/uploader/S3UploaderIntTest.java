package deepdivers.community.utility.uploader;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import deepdivers.community.global.config.localstack.SimpleLocalStackContainer;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Testcontainers
class S3UploaderIntTest {

    @Container
    public static LocalStackContainer localStack =
            new SimpleLocalStackContainer(DockerImageName.parse("localstack/localstack:latest")).withServices(S3);

    private S3Uploader s3Uploader;
    private S3Client s3Client;

    @BeforeEach
    void setUp() {
        s3Client = S3Client.builder()
                .endpointOverride(localStack.getEndpointOverride(S3))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        localStack.getAccessKey(), localStack.getSecretKey()
                )))
                .region(Region.of(localStack.getRegion()))
                .build();

        s3Client.createBucket(b -> b.bucket("test-bucket"));

        s3Uploader = new S3Uploader(
                "test-bucket",
                localStack.getEndpointOverride(S3).toString(),
                localStack.getRegion(),
                s3Client
        );
    }

    @Test
    @DisplayName("S3 이미지 업로드를 통합 테스트 한다.")
    void profileImageUploadSuccessfully() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes()
        );
        Long memberId = 1L;

        // When
        String result = s3Uploader.profileImageUpload(file, memberId);

        // Then
        assertTrue(result.contains("test-bucket"));
        assertTrue(result.contains("profiles/1/"));
        assertTrue(result.endsWith(".jpg"));
    }

    @Test
    @DisplayName("S3 이미지 업로드 시 이미지 파일이 아닐 경우 예외가 발생한다.")
    void InvalidImageUploadShouldBadRequestException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "text/plain", "test image content".getBytes()
        );
        Long memberId = 1L;

        // When, Then
        assertThatThrownBy(() -> s3Uploader.profileImageUpload(file, memberId))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", S3Exception.INVALID_IMAGE);
    }

    @Test
    @DisplayName("S3 이미지 업로드 시 이미지 파일이 없을 경우 예외가 발생한다.")
    void InvalidImageUploadShouldNotFoundException() {
        // Given
        Long memberId = 1L;

        // When, Then
        assertThatThrownBy(() -> s3Uploader.profileImageUpload(null, memberId))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("exceptionType", S3Exception.NOT_FOUND_FILE);
    }

}