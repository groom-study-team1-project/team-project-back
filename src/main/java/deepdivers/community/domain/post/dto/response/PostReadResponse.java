package deepdivers.community.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import deepdivers.community.domain.post.model.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "게시글 조회 응답")
@JsonIgnoreProperties(ignoreUnknown = true) // 불필요한 필드 무시
public record PostReadResponse(
	Long postId,
	String title,
	String content,
	String thumbnail,
	Long categoryId,
	MemberInfo memberInfo,
	CountInfo countInfo,
	List<String> hashtags,
	List<String> imageUrls,
	String createdAt
) {
	public static PostReadResponse from(Post post) {
		return new PostReadResponse(
			post.getId(),
			post.getTitle().getTitle(),
			post.getContent().getContent(),
			post.getThumbnail(),
			post.getCategory().getId(),
			new MemberInfo(
				post.getMember().getId(),
				post.getMember().getNickname(),
				post.getMember().getImageKey(),
				post.getMember().getJob()
			),
			new CountInfo(
				post.getViewCount(),
				post.getLikeCount(),
				post.getCommentCount()
			),
			post.getHashtags(),
			post.getImageKeys(),
			post.getCreatedAt().toString()
		);
	}
}
