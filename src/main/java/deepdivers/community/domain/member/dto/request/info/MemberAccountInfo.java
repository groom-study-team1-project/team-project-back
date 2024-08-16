package deepdivers.community.domain.member.dto.request.info;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 계정")
public record MemberAccountInfo(
        @Schema(description = "사용자 이메일", example = "test@mail.com")
        String email,
        @Schema(description = "사용자 비밀번호", example = "test1234!")
        String password
) {
}
