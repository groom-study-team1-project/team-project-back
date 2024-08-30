package deepdivers.community.utility.uploader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

class S3UploaderUnitTest {

    private S3Uploader s3Uploader;
    private S3Client s3ClientMock;
    private S3Utilities s3UtilitiesMock;

    @BeforeEach
    void setUp() {
        s3ClientMock = mock(S3Client.class);
        s3UtilitiesMock = mock(S3Utilities.class);
        when(s3ClientMock.utilities()).thenReturn(s3UtilitiesMock);
        s3Uploader = new S3Uploader("test-bucket", s3ClientMock);
    }

    @Test
    @DisplayName("S3 이미지 업로드를 단위테스트 한다.")
    void uploadSuccessfully() throws IOException, URISyntaxException {
        // Given
        MockMultipartFile file = generateMockMultipartFile();
        Long memberId = 1L;
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/profiles/1/test.jpg";

        when(s3ClientMock.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());
        when(s3UtilitiesMock.getUrl(any(Consumer.class))).thenReturn(new URI(expectedUrl).toURL());

        // When
        String uploadedUrl = s3Uploader.upload(file, memberId);

        // Then
        verify(s3ClientMock).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        Assertions.assertThat(uploadedUrl).isEqualTo(expectedUrl);
    }

    private MockMultipartFile generateMockMultipartFile() {
        String fileName = "test.jpg";
        String contentType = "image/jpeg";
        byte[] content = "test image content".getBytes();
        return new MockMultipartFile("file", fileName, contentType, content);
    }

}