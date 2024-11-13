package deepdivers.community.utility.uploader;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
import deepdivers.community.global.utility.uploader.S3Exception;
import deepdivers.community.global.utility.uploader.S3Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
@Import(LocalStackTestConfig.class)
class S3UploaderIntTest {

    @Autowired
    private S3Client s3Client;

    private S3Uploader s3Uploader;

    @BeforeEach
    void setUp() {
        s3Uploader = new S3Uploader(
            "test-bucket",
            "http://localhost:4566",
            "ap-northeast-2",
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
