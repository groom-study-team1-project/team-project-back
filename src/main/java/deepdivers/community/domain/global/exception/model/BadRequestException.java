package deepdivers.community.domain.global.exception.model;

public class BadRequestException extends BaseException {

    public BadRequestException(final ExceptionType exceptionType) {
        super(exceptionType);
    }

}
