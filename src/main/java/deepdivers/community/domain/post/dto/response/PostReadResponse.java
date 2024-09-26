package deepdivers.community.domain.post.dto.response;

import deepdivers.community.domain.post.model.Post;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 조회 응답")
public record PostReadResponse(
	Long postId,
	String title,
	String content,
	String category,
	Integer viewCount,
	Integer likeCount
) {
	public static PostReadResponse from(Post post) {
		return new PostReadResponse(
			post.getId(),
			post.getTitle().getTitle(),
			post.getContent().getContent(),
			post.getCategory().getName(),
			post.getViewCount(),
			post.getLikeCount()
		);
	}
}
