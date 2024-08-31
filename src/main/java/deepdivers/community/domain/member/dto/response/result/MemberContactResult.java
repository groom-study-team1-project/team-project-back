package deepdivers.community.domain.member.dto.response.result;

import deepdivers.community.domain.member.model.Contact;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 연락처 정보")
public record MemberContactResult(
        @Schema(description = "사용자 전화번호", example = "010-1234-5678")
        String phoneNumber,
        @Schema(description = "사용자 깃허브 주소", example = "https://github.com")
        String githubUrl,
        @Schema(description = "사용자 블로그 주소", example = "https://velog.io")
        String blogUrl
) {

    public static MemberContactResult from(final Contact contact) {
        return new MemberContactResult(
                contact.getPhoneNumber(),
                contact.getGithubAddr(),
                contact.getBlogAddr()
        );
    }

}
