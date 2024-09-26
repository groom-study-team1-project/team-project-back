package deepdivers.community.domain.post.dto.request;

public record WriteCommentRequest(
    Long postId,
    String content
) {
}
