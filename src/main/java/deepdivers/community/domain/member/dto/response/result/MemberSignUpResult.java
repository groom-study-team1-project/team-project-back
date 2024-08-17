package deepdivers.community.domain.member.dto.response.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import deepdivers.community.domain.member.model.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "사용자 회원가입 결과")
public record MemberSignUpResult(
        @Schema(description = "회원가입 순서", example = "1")
        Long id,
        @Schema(description = "회원 닉네임", example = "구름이")
        String nickname,
        @Schema(description = "회원가입일", example = "1900-01-01 00:00:00", type = "string")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {

    public static MemberSignUpResult from(final Account account) {
        return new MemberSignUpResult(account.getId(), account.getMember().getNickname(), account.getCreatedAt());
    }

}
