package deepdivers.community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PopularPostResponse {
    private Long postId;
    private String title;
    private String content;
    private String thumbnailUrl;
}
