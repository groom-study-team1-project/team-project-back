package deepdivers.community.utility.uploader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3Uploader {

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
        final String fileName = parseSaveFileName(Objects.requireNonNull(file.getOriginalFilename()));
        final String key = String.format("profiles/%d/%s", memberId, fileName);
        final PutObjectRequest putObjectRequest = getPutObjectRequest(file, key);

        profileImageUpload(putObjectRequest, file);

        return String.format("%s/%s", baseUrl, key);
    }

    private PutObjectRequest getPutObjectRequest(final MultipartFile file, final String key) {
        return PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();
    }

    private String parseSaveFileName(final String originalFilename) {
        final String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        final String fileBaseName = UUID.randomUUID().toString().substring(0, 8);
        return fileBaseName + "_" + System.currentTimeMillis() + fileExtension;
    }

    private void profileImageUpload(final PutObjectRequest putObjectRequest, final MultipartFile file) {
        try (final InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}