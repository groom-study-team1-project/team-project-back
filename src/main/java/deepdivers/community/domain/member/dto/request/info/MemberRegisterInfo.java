package deepdivers.community.domain.member.dto.request.info;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "사용자 회원가입 정보")
public record MemberRegisterInfo(
        @Schema(description = "사용자 닉네임", example = "구름이")
        String nickname,
        @Schema(description = "사용자 이미지", example = "http://localhost:8080/images/profile.png")
        String imageUrl,
        @Schema(description = "사용자 전화번호", example = "010-0000-0000")
        String tel
) {
}
