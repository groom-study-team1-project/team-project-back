package deepdivers.community.domain.member.dto.response.result;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 프로필 업로드 결과")
public record ProfileImageUploadResult(
        @Schema(description = "프로필 이미지 주소", example = "http://imageurl.png")
        String imageUrl
){
}
