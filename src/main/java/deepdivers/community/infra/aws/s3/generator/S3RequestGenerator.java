package deepdivers.community.infra.aws.s3.generator;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class S3RequestGenerator {

    private static final Duration PRE_SIGNED_URL_EXPIRATION = Duration.ofMinutes(5);

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
            .build();
    }

}