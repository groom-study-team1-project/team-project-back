package deepdivers.community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ProjectPostDetailResponse extends ProjectPostPreviewResponse {

    private boolean isLikedMe;
    private boolean isWroteMe;

}
