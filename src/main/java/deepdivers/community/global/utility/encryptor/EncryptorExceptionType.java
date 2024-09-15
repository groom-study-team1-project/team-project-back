package deepdivers.community.global.utility.encryptor;

import deepdivers.community.global.exception.model.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EncryptorExceptionType implements ExceptionType {

    NOT_FOUND_SHA_256_ALGORITHM(500, "SHA-256 알고리즘을 찾을 수 없습니다.");

    private final int code;
    private final String message;

}
