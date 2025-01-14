package deepdivers.community.domain.post.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectPostPreviewResponse extends PostPreviewResponse {

    private List<String> slideImageUrls;

}
