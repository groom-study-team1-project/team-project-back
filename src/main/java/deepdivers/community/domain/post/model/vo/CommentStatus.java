package deepdivers.community.domain.post.model.vo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommentStatus {

    REGISTERED("등록"),
    UNREGISTERED("해지");

    private final String description;

}
