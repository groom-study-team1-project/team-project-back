package deepdivers.community.infra.aws.s3.validator;

import static deepdivers.community.infra.aws.s3.exception.S3Exception.INVALID_IMAGE_FORMAT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.common.exception.BadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class S3FileValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"image", "jpg", "anyString"})
    @NullAndEmptySource
    void givenInvalidContentTypeWhenValidateContentTypeThenThrowException(String contentType) {
        // given
        // when & then
        assertThatThrownBy(() -> S3FileValidator.validateContentType(contentType))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", INVALID_IMAGE_FORMAT);
    }

}