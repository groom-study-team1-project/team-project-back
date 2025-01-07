package deepdivers.community.domain.post.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponse extends PostPreviewResponse {

    private List<String> imageUrls;
    private boolean isLikedMe;
    private boolean isWroteMe;

}
