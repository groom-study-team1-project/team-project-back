package deepdivers.community.infra.aws.s3;


import deepdivers.community.infra.aws.s3.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

@Component
@RequiredArgsConstructor
public class S3TagManager {

    private static final String LIFECYCLE_TAG_KEY = "Status";
    private static final String PERMANENT_TAG_VALUE = "Permanent";

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public void markAsUsed(final String key) {
        final PutObjectTaggingRequest request = PutObjectTaggingRequest.builder()
            .bucket(s3Properties.getBucket())
            .key(key)
            .tagging(createPermanentTag())
            .build();

        s3Client.putObjectTagging(request);
    }

    public void markAsUnused(final String key) {
        final DeleteObjectTaggingRequest request = DeleteObjectTaggingRequest.builder()
            .bucket(s3Properties.getBucket())
            .key(key)
            .build();

        s3Client.deleteObjectTagging(request);
    }

    private static Tagging createPermanentTag() {
        return Tagging.builder()
            .tagSet(Tag.builder()
                .key(LIFECYCLE_TAG_KEY)
                .value(PERMANENT_TAG_VALUE)
                .build())
            .build();
    }

}