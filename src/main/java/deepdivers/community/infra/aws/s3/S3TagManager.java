package deepdivers.community.infra.aws.s3;


import static deepdivers.community.infra.aws.s3.exception.S3Exception.NOT_FOUND_FILE;

import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.infra.aws.s3.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

@Component
@RequiredArgsConstructor
public class S3TagManager {

    private static final String LIFECYCLE_TAG_KEY = "Status";
    private static final String DELETE_TAG_VALUE = "Deleted";

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public void validateDoesNotObjectExist(final String key) {
        if (doesNotObjectExist(key)) {
            throw new BadRequestException(NOT_FOUND_FILE);
        }
    }
    public boolean doesNotObjectExist(final String key) {
        try {
            final HeadObjectRequest headObjectRequest = getHeadObjectRequest(s3Properties.getBucket(), key);
            s3Client.headObject(headObjectRequest);

            return false;
        } catch (final NoSuchKeyException e) {
            return true;
        }
    }

    private static HeadObjectRequest getHeadObjectRequest(final String bucket, final String key) {
        return HeadObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
    }

    public void markAsUsed(final String key) {
        validateDoesNotObjectExist(key);

        final PutObjectTaggingRequest request = PutObjectTaggingRequest.builder()
            .bucket(s3Properties.getBucket())
            .key(key)
            .tagging(createPermanentTag())
            .build();

        s3Client.putObjectTagging(request);
    }

    public void markAsUnused(final String key) {
        validateDoesNotObjectExist(key);

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
                .value(DELETE_TAG_VALUE)
                .build())
            .build();
    }

}