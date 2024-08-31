package deepdivers.community.domain.member.dto.response.result;

import deepdivers.community.domain.member.model.ActivityStats;
import deepdivers.community.domain.member.model.Contact;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.model.Nickname;
import deepdivers.community.domain.member.model.Password;
import deepdivers.community.domain.member.model.vo.MemberRole;
import deepdivers.community.domain.member.model.vo.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Schema(description = "사용자 프로필 조회 결과")
public record MemberProfileResult(
        @Schema(description = "사용자 닉네임", example = "구름이")
        String nickname,
        @Schema(description = "사용자 역할", example = "NORMAL, STUDENT, GRADUATE")
        String role,
        @Schema(description = "사용자 이미지", example = "image.png")
        String imageUrl,
        @Schema(description = "사용자 소개", example = "안녕하세요. 구름톤 딥다이브 수강생입니다.")
        String aboutMe,
        @Schema(description = "사용자 연락처 정보")
        MemberContactResult contact,
        @Schema(description = "사용자 활동 통계")
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
