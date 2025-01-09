package deepdivers.community.domain.common.exception;

import deepdivers.community.domain.common.dto.code.ExceptionCode;

public class NotFoundException extends BaseException {

    public NotFoundException(final ExceptionCode exceptionType) {
        super(exceptionType);
    }

}
