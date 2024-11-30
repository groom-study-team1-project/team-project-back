package deepdivers.community.global.utility.uploader;

import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3Uploader {

    private static final String TEMP_BUCKET_NAME = "temp";
    private static final String POST_BUCKET_NAME = "posts";

    public static final String TEMP_DIRECTORY = String.format("/%s/", S3Uploader.TEMP_BUCKET_NAME);
    public static final String POST_DIRECTORY = String.format("/%s/", S3Uploader.POST_BUCKET_NAME);

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");

    private final String bucket;
    private final String baseUrl;
    private final S3Client s3Client;

    public S3Uploader(
            @Value("${spring.cloud.aws.s3.bucket}")
            final String bucket,
            @Value("${spring.cloud.aws.s3.endpoint}")
            final String baseUrl,
            @Value("${spring.cloud.aws.region.static}")
            final String region,
            final S3Client s3Client
    ) {
        this.bucket = bucket;
        this.s3Client = s3Client;
        if (baseUrl.isEmpty()) {
            this.baseUrl = String.format("https://%s.s3.%s.amazonaws.com", bucket, region);
        } else {
            this.baseUrl = String.format("%s/%s", baseUrl, bucket);
        }
    }

    public String profileImageUpload(final MultipartFile file, final Long memberId) {
        validateImageFile(file);

        final String fileName = parseSaveFileName(file);
        final String key = String.format("profiles/%d/%s", memberId, fileName);
        uploadToS3(getPutObjectRequest(file, key), file);

        return String.format("%s/%s", baseUrl, key);
    }

    public String postImageUpload(final MultipartFile file) {
        validateImageFile(file);

        final String fileName = parseSaveFileName(file);
        final String key = String.format("%s/%s", TEMP_BUCKET_NAME, fileName);
        uploadToS3(getPutObjectRequest(file, key), file);

        return String.format("%s/%s", baseUrl, key);
    }

    public String moveImage(String sourceKey, String destinationKey) {
        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(sourceKey)
                .destinationBucket(bucket)
                .destinationKey(destinationKey)
                .build();

        s3Client.copyObject(copyReq);

        return String.format("%s/%s", baseUrl, destinationKey);
    }

    public String buildTempKey(String fileName) {
        return String.format("%s/%s", TEMP_BUCKET_NAME, fileName);
    }

    public String buildPostKey(Long postId, String fileName) {
        return String.format("%s/%d/%s", POST_BUCKET_NAME, postId, fileName);
    }

    private void validateImageFile(final MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new NotFoundException(S3Exception.NOT_FOUND_FILE);
        }

        final String contentType = file.getContentType();
        if (Objects.isNull(contentType) || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new BadRequestException(S3Exception.INVALID_IMAGE);
        }

        final String extension = getExtension(file).substring(1);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(S3Exception.INVALID_IMAGE);
        }
    }

    private PutObjectRequest getPutObjectRequest(final MultipartFile file, final String key) {
        return PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();
    }

    private String getExtension(final MultipartFile file) {
        final String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    private String parseSaveFileName(final MultipartFile file) {
        final String fileExtension = getExtension(file);
        final String fileBaseName = UUID.randomUUID().toString().substring(0, 8);
        return fileBaseName + "_" + System.currentTimeMillis() + fileExtension;
    }

    private void uploadToS3(final PutObjectRequest putObjectRequest, final MultipartFile file) {
        try (final InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
