package deepdivers.community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // Add this annotation to create a no-arg constructor
@AllArgsConstructor // Existing constructor with arguments
public class MemberInfo {
	private Long memberId;
	private String nickname;
	private String imageUrl;
	private String memberJob;
}
