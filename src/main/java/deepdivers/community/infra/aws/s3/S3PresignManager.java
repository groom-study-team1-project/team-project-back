package deepdivers.community.infra.aws.s3;

import static deepdivers.community.infra.aws.s3.validator.S3FileValidator.validateContentType;

import deepdivers.community.infra.aws.s3.generator.S3KeyGenerator;
import deepdivers.community.infra.aws.s3.properties.S3Properties;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@RequiredArgsConstructor
public class S3PresignManager {

    private static final Duration PRE_SIGNED_URL_EXPIRATION = Duration.ofMinutes(5);
    private static final String TAG_INITIAL = "Status=Deleted";

    private final S3Properties s3Properties;
    private final S3Presigner s3Presigner;

    public String generateKey(final String contentType, final KeyType keyType) {
        validateContentType(contentType);

        final String uuid = UUID.randomUUID().toString();
        return switch (keyType) {
            case POST -> S3KeyGenerator.generatePostKey(contentType, uuid);
            case PROFILE -> S3KeyGenerator.generateProfileKey(contentType, uuid);
        };
    }

    public String generatePreSignedUrl(final String key, final String contentType) {
        final PutObjectRequest objectRequest = generatePutObjectRequest(key, contentType, s3Properties.getBucket());
        final PutObjectPresignRequest presignRequest = generatePresignRequest(objectRequest);

        return s3Presigner.presignPutObject(presignRequest)
            .url()
            .toString();
    }

    public String generateAccessUrl(final String key) {
        return String.format("%s/%s", s3Properties.getBaseUrl(), key);
    }

    public static PutObjectPresignRequest generatePresignRequest(final PutObjectRequest objectRequest) {
        return PutObjectPresignRequest.builder()
            .signatureDuration(PRE_SIGNED_URL_EXPIRATION)
            .putObjectRequest(objectRequest)
            .build();
    }

    public static PutObjectRequest generatePutObjectRequest(
        final String key,
        final String contentType,
        final String bucket
    ) {
        return PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .tagging(TAG_INITIAL)
            .build();
    }

}
