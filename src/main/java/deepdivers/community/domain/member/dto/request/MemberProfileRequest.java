package deepdivers.community.domain.member.dto.request;

import deepdivers.community.domain.member.model.Nickname;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 프로필 수정하기")
public record MemberProfileRequest(
    @Schema(description = "사용자 닉네임", example = "구름이")
    String nickname,
    @Schema(description = "사용자 이미지", example = "http://localhost:4566/image")
    String imageUrl,
    @Schema(description = "사용자 소개", example = "안녕하세요.")
    String aboutMe,
    @Schema(description = "사용자 전화번호", example = "010-1234-5678")
    String phoneNumber,
    @Schema(description = "사용자 깃허브", example = "https://github.com")
    String githubUrl,
    @Schema(description = "사용자 블로그", example = "https://velog.io")
    String blogUrl
) {
}
