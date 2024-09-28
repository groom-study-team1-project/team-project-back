package deepdivers.community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountInfo {
	private Integer viewCount;
	private Integer likeCount;
	private Integer commentCount;
}
