package deepdivers.community.infra.aws.s3;

import deepdivers.community.infra.aws.s3.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Component
@RequiredArgsConstructor
public class S3ObjectInspector {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public boolean doesObjectExist(final String key) {
        try {
            final HeadObjectRequest headObjectRequest = getHeadObjectRequest(s3Properties.getBucket(), key);
            s3Client.headObject(headObjectRequest);

            return true;
        } catch (final NoSuchKeyException e) {
            return false;
        }
    }

    private static HeadObjectRequest getHeadObjectRequest(final String bucket, final String key) {
        return HeadObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
    }

}