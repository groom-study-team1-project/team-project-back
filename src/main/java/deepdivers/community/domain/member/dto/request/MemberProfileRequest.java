package deepdivers.community.domain.member.dto.request;

import deepdivers.community.domain.member.model.Nickname;

public record MemberProfileRequest(
    String nickname,
    String imageUrl,
    String aboutMe,
    String phoneNumber,
    String githubUrl,
    String blogUrl
) {
}
