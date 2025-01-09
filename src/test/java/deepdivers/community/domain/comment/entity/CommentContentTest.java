package deepdivers.community.domain.comment.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.comment.exception.CommentExceptionCode;
import deepdivers.community.domain.common.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CommentContentTest {

    @Test
    void 댓글_생성() {
        CommentContent commentContent = new CommentContent("content");
        assertThat(commentContent.getValue()).isEqualTo("content");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 댓글_생성_예외(String comment) {
        assertThatThrownBy(() -> new CommentContent(comment))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CommentExceptionCode.INVALID_COMMENT_CONTENT);
    }

    @Test
    void 댓글_경계_예외() {
        String content = "1".repeat(101);

        assertThatThrownBy(() -> new CommentContent(content))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CommentExceptionCode.INVALID_COMMENT_CONTENT);
    }

}