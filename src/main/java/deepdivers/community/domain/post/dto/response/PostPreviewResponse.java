package deepdivers.community.domain.post.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostPreviewResponse {

	private Long postId;
	private Long categoryId;
	private String title;
	private String content;
	private String thumbnail;
	private List<String> hashtags;
	private Integer viewCount;
	private Integer likeCount;
	private Integer commentCount;
	private LocalDateTime createdAt;
	private AuthorInformationResponse authorInformation;

}

