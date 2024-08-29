package deepdivers.community.domain.member.dto.response.result;

import deepdivers.community.domain.member.model.Contact;

public record MemberContactResult(
        String phoneNumber,
        String githubUrl,
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
