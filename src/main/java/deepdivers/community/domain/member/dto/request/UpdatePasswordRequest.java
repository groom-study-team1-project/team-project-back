package deepdivers.community.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequest(
    @Schema(description = "현재 비밀번호", example = "password1!")
    @NotNull(message = "현재 비밀번호 정보가 필요합니다.")
    String currentPassword,
    @Schema(description = "새로운 비밀번호", example = "password2!")
    @NotNull(message = "새로운 비밀번호 정보가 필요합니다.")
    String newPassword
) {
}
