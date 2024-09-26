package deepdivers.community.domain.post.model.comment;

import deepdivers.community.domain.common.Content;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentContent extends Content {

    private static final int NAX_COMMENT_LENGTH = 100;

    public CommentContent(final String contentText) {
        super(contentText);
    }

    @Override
    protected void checkText(final String contentText) {
        if (contentText == null || contentText.isEmpty() || contentText.length() > NAX_COMMENT_LENGTH) {
            throw new IllegalArgumentException();
        }
    }

}
