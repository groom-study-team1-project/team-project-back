package deepdivers.community.domain.post.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostAllReadResponse {
	private Long postId;
	private String title;
	private String content;
	private Long categoryId;
	private MemberInfo memberInfo;
	private CountInfo countInfo;
	private List<String> hashtags;
	private List<String> imageUrls;
	private String createdAt;

	public static PostAllReadResponse of(
			Long postId,
			String title,
			String content,
			Long categoryId,
			MemberInfo memberInfo,
			CountInfo countInfo,
			List<String> hashtags,
			List<String> imageUrls,
			String createdAt
	) {
		return new PostAllReadResponse(
				postId,
				title,
				content,
				categoryId,
				memberInfo,
				countInfo,
				hashtags,
				imageUrls != null ? imageUrls : List.of(),
				createdAt
		);
	}
}

