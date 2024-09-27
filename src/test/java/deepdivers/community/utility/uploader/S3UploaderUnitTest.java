package deepdivers.community.utility.uploader;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import deepdivers.community.domain.global.utility.uploader.S3Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

class S3UploaderUnitTest {

    private S3Uploader s3Uploader;
    private S3Client s3ClientMock;

    @BeforeEach
    void setUp() {
        s3ClientMock = mock(S3Client.class);
        s3Uploader = new S3Uploader("test-bucket", "http://localhost:4566", "us-east-1", s3ClientMock);
    }

    @Test
    @DisplayName("S3 이미지 업로드를 단위테스트 한다.")
    void profileImageUploadSuccessfully() {
        // Given
        MockMultipartFile file = generateMockMultipartFile();
        Long memberId = 1L;

        // When
        String uploadedUrl = s3Uploader.profileImageUpload(file, memberId);

        // Then
        ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3ClientMock).putObject(putObjectRequestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = putObjectRequestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo("test-bucket");
        assertThat(capturedRequest.key()).startsWith("profiles/1/");
        assertThat(capturedRequest.key()).endsWith(".jpg");

        assertThat(uploadedUrl).startsWith("http://localhost:4566/test-bucket/profiles/1/");
        assertThat(uploadedUrl).endsWith(".jpg");
    }

    private MockMultipartFile generateMockMultipartFile() {
        String fileName = "test.jpg";
        String contentType = "image/jpeg";
        byte[] content = "test image content".getBytes();
        return new MockMultipartFile("file", fileName, contentType, content);
    }

}
