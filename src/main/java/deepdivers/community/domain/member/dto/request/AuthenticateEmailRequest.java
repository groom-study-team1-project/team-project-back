package deepdivers.community.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticateEmailRequest(
    @Schema(description = "사용자 이메일", example = "test@mail.com")
    @NotBlank(message = "사용자 이메일 정보가 필요합니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    String email
) {
}
