package deepdivers.community.utility.uploader;

import deepdivers.community.global.exception.model.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S3Exception implements ExceptionType {

    NOT_FOUND_FILE(9100, "업로드 파일을 찾을 수 없습니다."),
    INVALID_IMAGE(9101, "이미지 파일이 아닙니다.");

    private final int code;
    private final String message;
}
