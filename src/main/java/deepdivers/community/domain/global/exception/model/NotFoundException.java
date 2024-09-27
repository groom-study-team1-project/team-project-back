package deepdivers.community.domain.global.exception.model;

public class NotFoundException extends BaseException {

    public NotFoundException(final ExceptionType exceptionType) {
        super(exceptionType);
    }

}
