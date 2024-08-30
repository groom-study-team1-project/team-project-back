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
public class S3ImageUploader {

    private final String bucket;
    private final S3Client amazonS3;

    public S3ImageUploader(
            @Value("${spring.cloud.aws.s3.bucket}")
            final String bucket,
            final S3Client amazonS3
    ) {
        this.bucket = bucket;
        this.amazonS3 = amazonS3;
    }

    public String upload(final MultipartFile file, final Long memberId) {
        final String fileName = parseSaveFileName(Objects.requireNonNull(file.getOriginalFilename()));
        final String key = String.format("profiles/%d/%s", memberId, fileName);
        final PutObjectRequest putObjectRequest = getPutObjectRequest(file, key);

        amazonS3.putObject(putObjectRequest, getRequestBody(file));

        return amazonS3.utilities()
                .getUrl(builder -> builder.bucket(bucket).key(key))
                .toString();
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

    private RequestBody getRequestBody(final MultipartFile file) {
        try (final InputStream inputStream = file.getInputStream()) {
            return RequestBody.fromInputStream(inputStream, file.getSize());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}