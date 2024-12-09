package deepdivers.community.infra.aws.s3;

import static deepdivers.community.infra.aws.s3.exception.S3Exception.NOT_FOUND_FILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.infra.aws.s3.properties.S3Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@SpringBootTest
@Import(LocalStackTestConfig.class)
class S3TagManagerTest {

    @Autowired
    private S3TagManager s3TagManager;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Properties s3Properties;

    @Test
    @DisplayName("S3에 저장된 객체에 태그를 설정한다.")
    void markAsDeletedTest() throws Exception {
        // Given
        String key = "test/example.txt";
        createTestObject(key);

        // When
        s3TagManager.markAsDeleted(key);

        // Then
        GetObjectTaggingRequest getTaggingRequest = GetObjectTaggingRequest.builder()
            .bucket(s3Properties.getBucket())
            .key(key)
            .build();

        GetObjectTaggingResponse response = s3Client.getObjectTagging(getTaggingRequest);

        assertThat(response.tagSet()).hasSize(1);
        assertThat(response.tagSet().getFirst().key()).isEqualTo("Status");
        assertThat(response.tagSet().getFirst().value()).isEqualTo("Deleted");
    }

    @Test
    @DisplayName("S3에 저장된 객체에 태그를 제거한다.")
    void removeDeleteTagTest() throws Exception {
        // Given
        String key = "test/example.txt";
        createTestObject(key);
        s3TagManager.markAsDeleted(key);

        // When
        s3TagManager.removeDeleteTag(key);

        // Then
        GetObjectTaggingRequest getTaggingRequest = GetObjectTaggingRequest.builder()
            .bucket(s3Properties.getBucket())
            .key(key)
            .build();

        GetObjectTaggingResponse response = s3Client.getObjectTagging(getTaggingRequest);
        assertThat(response.tagSet()).isEmpty();
    }

    @Test
    @DisplayName("S3 저장소에 key 값이 존재할 경우 예외가 발생하지 않는다.")
    void validateDoesNotObjectExist_WhenObjectExists_ShouldNotThrowException() {
        // Given
        String key = "test/example.txt";
        createTestObject(key);

        // When & Then
        assertThatCode(() -> s3TagManager.validateDoesNotObjectExist(key))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("S3 저장소에 Key 값이 없을 경우 예외가 발생한다.")
    void validateDoesNotObjectExist_WhenObjectDoesNotExist_ShouldThrowException() {
        // Given
        String key = "test/non-existing.txt";

        // When & Then
        assertThatThrownBy(() -> s3TagManager.validateDoesNotObjectExist(key))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", NOT_FOUND_FILE);
    }

    private void createTestObject(String key) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.getBucket())
            .key(key)
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString("test content"));
    }

}