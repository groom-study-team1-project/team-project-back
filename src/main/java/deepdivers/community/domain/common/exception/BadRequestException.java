package deepdivers.community.domain.common.exception;

import deepdivers.community.domain.common.dto.code.ExceptionCode;

public class BadRequestException extends BaseException {

    public BadRequestException(final ExceptionCode exceptionType) {
        super(exceptionType);
    }

}
