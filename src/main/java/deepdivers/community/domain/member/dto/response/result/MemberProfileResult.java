package deepdivers.community.domain.member.dto.response.result;

import deepdivers.community.domain.member.model.ActivityStats;
import deepdivers.community.domain.member.model.Contact;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.model.Nickname;
import deepdivers.community.domain.member.model.Password;
import deepdivers.community.domain.member.model.vo.MemberRole;
import deepdivers.community.domain.member.model.vo.MemberStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record MemberProfileResult(
        String nickname,
        String role,
        String imageUrl,
        String aboutMe,
        MemberContactResult contact,
        MemberActivityStatsResult activityStats
) {

    public static MemberProfileResult from(final Member member) {
        return new MemberProfileResult(
                member.getNickname(),
                member.getRole().toString(),
                member.getImageUrl(),
                member.getAboutMe(),
                MemberContactResult.from(member.getContact()),
                MemberActivityStatsResult.from(member.getActivityStats())
        );
    }

}
