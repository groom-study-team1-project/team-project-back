package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 프로필 조회 결과")
public record MemberProfileResponse(
        @Schema(description = "사용자 닉네임", example = "구름이")
        String nickname,
        @Schema(description = "사용자 역할", example = "NORMAL, STUDENT, GRADUATE")
        String role,
        @Schema(description = "사용자 이미지", example = "image.png")
        String imageUrl,
        @Schema(description = "사용자 소개", example = "안녕하세요. 구름톤 딥다이브 수강생입니다.")
        String aboutMe,
        @Schema(description = "사용자 연락처 정보")
        MemberContactResponse contact,
        @Schema(description = "사용자 활동 통계")
        MemberActivityStatsResponse activityStats
) {

    public static MemberProfileResponse from(final Member member) {
        return new MemberProfileResponse(
                member.getNickname(),
                member.getRole().toString(),
                member.getImageUrl(),
                member.getAboutMe(),
                MemberContactResponse.from(member.getContact()),
                MemberActivityStatsResponse.from(member.getActivityStats())
        );
    }

}
