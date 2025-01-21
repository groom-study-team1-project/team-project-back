package deepdivers.community.domain.post.dto.response;

import deepdivers.community.domain.hashtag.dto.PopularHashtagResponse;
import java.util.List;

public record NormalPostPageResponse(
    List<PopularHashtagResponse> popularHashtags,
    List<PopularPostResponse> popularPosts,
    List<PostPreviewResponse> top10Posts
) {
}
