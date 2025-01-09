package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.member.entity.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "사용자 프로필 조회 결과")
@AllArgsConstructor
@Getter
public class MemberProfileResponse {

    @Schema(description = "사용자 식별자", example = "1")
    private final Long id;
    @Schema(description = "사용자 닉네임", example = "구름이")
    private final String nickname;
    @Schema(description = "사용자 역할", example = "NORMAL, STUDENT, GRADUATE")
    private final MemberRole role;
    @Schema(description = "사용자 이미지", example = "image.png")
    private String imageUrl;
    @Schema(description = "사용자 소개", example = "안녕하세요. 구름톤 딥다이브 수강생입니다.")
    private final String aboutMe;
    @Schema(description = "사용자 전화번호", example = "010-1234-5678")
    private final String phoneNumber;
    @Schema(description = "사용자 직업", example = "개발자")
    private final String job;
    @Schema(description = "사용자 깃허브 주소", example = "https://github.com")
    private final String githubUrl;
    @Schema(description = "사용자 블로그 주소", example = "https://velog.io")
    private final String blogUrl;
    @Schema(description = "사용자 게시글 수", example = "0")
    private final int postCount;
    @Schema(description = "사용자 댓글 수", example = "0")
    private final int commentCount;
    @Schema(description = "본인 식별", example = "false/true")
    private final boolean myProfile;

}
