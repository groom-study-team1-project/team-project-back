package deepdivers.community.domain.post.dto.response;

import deepdivers.community.domain.post.model.Post;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 생성 응답")
public record PostCreateResponse(
	Long postId
) {
	public static PostCreateResponse from(Post post) {
		return new PostCreateResponse(post.getId());
	}
}
