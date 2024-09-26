package deepdivers.community.domain.post.model.comment;

import deepdivers.community.domain.post.exception.CommentExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
@Getter
public class CommentContent {

    private static final int NAX_COMMENT_LENGTH = 100;

    @Column(name = "content", length = NAX_COMMENT_LENGTH, nullable = false)
    private String value;

    protected CommentContent(final String contentText) {
        checkText(contentText);
        this.value = contentText;
    }

    private void checkText(final String contentText) {
        if (contentText == null || contentText.isEmpty() || contentText.length() > NAX_COMMENT_LENGTH) {
            throw new BadRequestException(CommentExceptionType.INVALID_COMMENT_CONTENT);
        }
    }

    public void updateContent(final String contentText) {
        checkText(contentText);
        this.value = contentText;
    }

}
