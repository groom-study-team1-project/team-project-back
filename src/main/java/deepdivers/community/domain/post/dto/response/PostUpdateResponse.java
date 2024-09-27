package deepdivers.community.domain.post.dto.response;

import deepdivers.community.domain.post.model.Post;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 수정 응답")
public record PostUpdateResponse(
	Long postId,
	Long categoryId,
	String updatedTitle,
	String updatedContent
) {
	public static PostUpdateResponse from(Post post) {
		return new PostUpdateResponse(
			post.getId(),
			post.getCategory().getId(),
			post.getTitle().getTitle(),
			post.getContent().getContent()
		);
	}
}
