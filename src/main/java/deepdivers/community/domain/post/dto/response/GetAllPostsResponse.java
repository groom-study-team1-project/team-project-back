package deepdivers.community.domain.post.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllPostsResponse {

	private Long postId;
	private String title;
	private String content;
	private String thumbnail;
	private Long categoryId;
	private MemberInfo memberInfo;
	private CountInfo countInfo;
	private String hashtags;
	private String imageUrls;
	private LocalDateTime createdAt;

	public String imageUrls() {
		return imageUrls;
	}

	public List<String> getImageUrls() {
		return Optional.ofNullable(imageUrls)
			.filter(s -> !s.isEmpty())
			.map(s -> Arrays.asList(s.split(",")))
			.orElseGet(ArrayList::new);
	}

	public List<String> getHashtags() {
		return Optional.ofNullable(hashtags)
			.filter(s -> !s.isEmpty())
			.map(s -> Arrays.asList(s.split(",")))
			.orElseGet(ArrayList::new);
	}

}

