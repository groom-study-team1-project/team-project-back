package deepdivers.community.domain.common.exception;

import deepdivers.community.domain.common.dto.code.ExceptionCode;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final ExceptionCode exceptionType;

    public BaseException(ExceptionCode exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

}
