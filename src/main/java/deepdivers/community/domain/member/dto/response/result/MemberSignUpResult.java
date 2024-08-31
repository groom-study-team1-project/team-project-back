package deepdivers.community.domain.member.dto.response.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import deepdivers.community.domain.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "사용자 회원가입 결과")
public record MemberSignUpResult(
        @Schema(description = "회원가입 순서", example = "1")
        Long id,
        @Schema(description = "회원 닉네임", example = "구름이")
        String nickname,
        @Schema(description = "회원가입일", example = "2024-08-20T13:00:00")
        LocalDateTime createdAt
) {

    public static MemberSignUpResult from(final Member member) {
        return new MemberSignUpResult(member.getId(), member.getNickname(), member.getCreatedAt());
    }

}
