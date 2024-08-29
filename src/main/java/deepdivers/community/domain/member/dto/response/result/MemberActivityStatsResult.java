package deepdivers.community.domain.member.dto.response.result;

import deepdivers.community.domain.member.model.ActivityStats;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 활도 통계")
public record MemberActivityStatsResult(
        @Schema(description = "사용자 게시글 수", example = "0")
        int postCount,
        @Schema(description = "사용자 댓글 수", example = "0")
        int commentCount
) {

    public static MemberActivityStatsResult from(final ActivityStats activityStats) {
        return new MemberActivityStatsResult(
                activityStats.getPostCount(),
                activityStats.getCommentCount()
        );
    }

}
