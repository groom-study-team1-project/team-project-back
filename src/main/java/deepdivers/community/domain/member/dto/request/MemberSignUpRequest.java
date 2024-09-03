package deepdivers.community.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "사용자 회원가입 하기")
public record MemberSignUpRequest(
        @Schema(description = "사용자 이메일", example = "test@mail.com")
        @NotBlank(message = "사용자 이메일 정보가 필요합니다.")
        String email,

        @Schema(description = "사용자 비밀번호", example = "test1234!")
        @NotBlank(message = "사용자 비밀번호 정보가 필요합니다.")
        String password,

        @Schema(description = "사용자 닉네임", example = "구름이")
        @NotBlank(message = "사용자 닉네임 정보가 필요합니다.")
        String nickname,

        @Schema(description = "사용자 이미지", example = "http://localhost:8080/images/profile.png")
        @NotBlank(message = "사용자 이미지 정보가 필요합니다.")
        String imageUrl,

        @Schema(description = "사용자 전화번호", example = "010-0000-0000")
        @NotBlank(message = "사용자 전화번호 정보가 필요합니다.")
        String phoneNumber
) {
}
