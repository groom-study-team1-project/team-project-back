package deepdivers.community.infra.aws.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import deepdivers.community.infra.aws.s3.properties.S3Properties;
import java.net.MalformedURLException;
import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ExtendWith(MockitoExtension.class)
class S3PresignManagerTest {

    @Mock private S3Properties s3Properties;
    @Mock private S3Presigner s3Presigner;
    @InjectMocks private S3PresignManager s3PresignManager;

    @ParameterizedTest
    @EnumSource(KeyType.class)
    @DisplayName("키는 KeyType별 Prefix를 반환한다.")
    void givenKeyTypeWhenGenerateKeyThenReturnPrefixByKeyType(KeyType keyType) {
        // given
        // when
        String key = s3PresignManager.generateKey("image/jpeg", keyType);

        // then
        assertThat(key).startsWith(keyType.getPrefix());
    }

    @Test
    @DisplayName("주어진 키와 Content-Type으로 Pre-Signed URL을 생성한다.")
    void givenKeyAndContentTypeWhenGeneratePreSignedUrlThenReturnValidUrl() throws MalformedURLException {

        // given
        String expectedUrl = "https://test-url.com";

        PresignedPutObjectRequest presignedRequest = mock(PresignedPutObjectRequest.class);
        when(presignedRequest.url()).thenReturn(URI.create(expectedUrl).toURL());
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presignedRequest);

        // when
        String result = s3PresignManager.generatePreSignedUrl("profiles/test/image.jpg", "image/jpeg");

        // then
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("주어진 키로 Access URL을 생성한다.")
    void givenKeyWhenGenerateAccessUrlThenReturnValidUrl() {
        // given
        String key = "profiles/test/image.jpg";
        String baseUrl = "https://test-cdn.com";
        when(s3Properties.getBaseUrl()).thenReturn(baseUrl);

        // when
        String result = s3PresignManager.generateAccessUrl(key);

        // then
        assertThat(result).isEqualTo(baseUrl + "/" + key);
    }

}