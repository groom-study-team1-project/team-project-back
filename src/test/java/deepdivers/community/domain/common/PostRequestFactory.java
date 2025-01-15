package deepdivers.community.domain.common;

import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import java.util.List;

public class PostRequestFactory {

    public static PostSaveRequest createPostSaveRequest() {
        return new PostSaveRequest(
            "Post Title",
            "Post Content",
            "",
            1L,
            List.of("tag1", "tag2"),
            List.of("posts/image2.png", "posts/image3.png")
        );
    }

    public static ProjectPostRequest createProjectPostRequest() {
        return new ProjectPostRequest(
            "Post Title",
            "Post Content",
            "",
            1L,
            List.of("tag1", "tag2"),
            List.of("posts/image2.png", "posts/image3.png"),
            List.of("posts/image4.png", "posts/image5.png")
        );
    }
}
