package deepdivers.community.domain.member.dto.request.info;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "사용자 계정")
public record MemberAccountInfo(
        @Schema(description = "사용자 이메일", example = "test@mail.com")
        @NotBlank(message = "사용자 이메일 정보가 필요합니다.")
        @Email(message = "이메일 형식으로 입력해주세요.")
        String email,
        @Schema(description = "사용자 비밀번호", example = "test1234!")
        @NotBlank(message = "사용자 비밀번호 정보가 필요합니다.")
        String password
) {
}