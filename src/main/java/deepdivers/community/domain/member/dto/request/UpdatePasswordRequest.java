package deepdivers.community.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequest(
    @NotNull(message = "현재 비밀번호 정보가 필요합니다.")
    String currentPassword,
    @NotNull(message = "새로운 비밀번호 정보가 필요합니다.")
    String newPassword
) {
}
