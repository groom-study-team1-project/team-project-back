package deepdivers.community.domain.post.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCountResponse {
	private Long totalPostCount;  // 게시글 총 개수
	private List<GetAllPostsResponse> posts;  // 게시글 리스트
}
