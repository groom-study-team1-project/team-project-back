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
	private String createdAt;
	private List<String> imageUrls;
}
