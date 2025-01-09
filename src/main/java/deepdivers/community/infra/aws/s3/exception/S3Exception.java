package deepdivers.community.infra.aws.s3.exception;

import deepdivers.community.domain.common.dto.code.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S3Exception implements ExceptionCode {

    NOT_FOUND_FILE(9100, "업로드 파일을 찾을 수 없습니다."),
    INVALID_IMAGE_FORMAT(9101, "이미지 파일 형식이 아닙니다.");

    private final int code;
    private final String message;

}
