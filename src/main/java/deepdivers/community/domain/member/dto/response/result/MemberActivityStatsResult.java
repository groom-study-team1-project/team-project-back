package deepdivers.community.domain.member.dto.response.result;

import deepdivers.community.domain.member.model.ActivityStats;

public record MemberActivityStatsResult(
        int postCount,
        int commentCount
) {

    public static MemberActivityStatsResult from(final ActivityStats activityStats) {
        return new MemberActivityStatsResult(
                activityStats.getPostCount(),
                activityStats.getCommentCount()
        );
    }

}
