package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.member.model.ActivityStats;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 활동 통계")
public record MemberActivityStatsResponse(
        @Schema(description = "사용자 게시글 수", example = "0")
        int postCount,
        @Schema(description = "사용자 댓글 수", example = "0")
        int commentCount
) {

    public static MemberActivityStatsResponse from(final ActivityStats activityStats) {
        return new MemberActivityStatsResponse(
                activityStats.getPostCount(),
                activityStats.getCommentCount()
        );
    }

}
