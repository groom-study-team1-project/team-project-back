package deepdivers.community.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

        @Schema(description = "사용자 이미지 키", example = "profiles/002da67c_1730807352645.png")
        @NotBlank(message = "사용자 이미지 키 정보가 필요합니다.")
        String imageKey,

        @Schema(description = "사용자 전화번호", example = "010-1234-1234")
        @NotBlank(message = "사용자 전화번호 정보가 필요합니다.")
        String phoneNumber
) {
}
