package deepdivers.community.domain.global.exception.model;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final ExceptionType exceptionType;

    public BaseException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

}
