package deepdivers.community.domain.member.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import deepdivers.community.domain.member.model.Account;
import java.time.LocalDateTime;

public record MemberSignUpResult(
        String nickname,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {

    public static MemberSignUpResult from(final Account account) {
        return new MemberSignUpResult(
                account.getMember().getNickname(),
                account.getCreatedAt()
        );
    }

}
