package deepdivers.community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProjectPostDetailResponse extends ProjectPostPreviewResponse {

    private boolean isLikedMe;
    private boolean isWroteMe;

}
