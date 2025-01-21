package deepdivers.community.utils;

import deepdivers.community.domain.post.dto.response.AuthorInformationResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPreviewResponse;
import java.time.LocalDateTime;
import java.util.List;

public class PostDtoGenerator {

    public static PostPreviewResponse generatePostPreview() {
        return new PostPreviewResponse(
            1L, 1L, "test", "test", "test", List.of("test"), 0, 0, 0, LocalDateTime.now(),
            new AuthorInformationResponse(1L, "test", "test", "test")
        );
    }

    public static ProjectPostPreviewResponse generateProjectPostPreview() {
        return new ProjectPostPreviewResponse(List.of(""));
    }

    public static ProjectPostDetailResponse generateProjectDetail() {
        return new ProjectPostDetailResponse(false, false);
    }

}
