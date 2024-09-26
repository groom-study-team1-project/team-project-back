package deepdivers.community.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 작성자 정보")
public record MemberInfo(
	Long memberId,
	String nickname,
	String imageUrl
) {}
