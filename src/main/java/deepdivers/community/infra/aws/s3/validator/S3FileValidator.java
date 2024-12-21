package deepdivers.community.infra.aws.s3.validator;

import static deepdivers.community.infra.aws.s3.exception.S3Exception.INVALID_IMAGE_FORMAT;

import deepdivers.community.global.exception.model.BadRequestException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class S3FileValidator {

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");

    public static void validateContentType(final String contentType) {
        if (Objects.isNull(contentType) || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new BadRequestException(INVALID_IMAGE_FORMAT);
        }
    }

}
