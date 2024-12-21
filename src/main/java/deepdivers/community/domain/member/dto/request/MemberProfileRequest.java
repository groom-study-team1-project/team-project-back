package deepdivers.community.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "사용자 프로필 수정하기")
public record MemberProfileRequest(
    @Schema(description = "사용자 닉네임", example = "구름이")
    @NotBlank(message = "사용자 닉네임 정보가 필요합니다.")
    String nickname,
    @Schema(description = "사용자 이미지 키", example = "profiles/uuid/fileName.extension", nullable = true)
    String imageKey,
    @Schema(description = "사용자 소개", example = "안녕하세요.")
    String aboutMe,
    @Schema(description = "사용자 전화번호", example = "010-1234-5678")
    @NotBlank(message = "사용자 전화번호 정보가 필요합니다.")
    String phoneNumber,
    @Schema(description = "사용자 깃허브", example = "https://github.com")
    String githubUrl,
    @Schema(description = "사용자 블로그", example = "https://velog.io")
    String blogUrl,
    @Schema(description = "사용자 직업", example = "개발자")
    String job
) {
}
