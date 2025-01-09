package deepdivers.community.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.IntegrationTest;
import deepdivers.community.domain.comment.service.CommentService;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.comment.dto.request.EditCommentRequest;
import deepdivers.community.domain.comment.dto.request.RemoveCommentRequest;
import deepdivers.community.domain.comment.dto.request.WriteCommentRequest;
import deepdivers.community.domain.comment.dto.request.WriteReplyRequest;
import deepdivers.community.domain.comment.dto.code.CommentStatusType;
import deepdivers.community.domain.comment.exception.CommentExceptionType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.comment.entity.CommentStatus;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentServiceTest extends IntegrationTest {

    @Autowired
    CommentService commentService;

    @Test
    void 댓글_작성이_성공한다() {
        // given
        Member member = getMember(1L);
        WriteCommentRequest request = new WriteCommentRequest(1L, "댓글 작성");

        // when
        NoContent result = commentService.writeComment(member, request);

        // then
        NoContent expected = NoContent.from(CommentStatusType.COMMENT_CREATE_SUCCESS);
        assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    void 댓글_작성_후_게시글의_댓글_수가_증가한다() {
        // given
        Member member = getMember(1L);
        WriteCommentRequest request = new WriteCommentRequest(1L, "댓글 작성");
        Integer commentCount = getPost(1L).getCommentCount();

        // when
        commentService.writeComment(member, request);

        // then
        cacheClear();
        assertThat(getPost(1L).getCommentCount()).isEqualTo((commentCount + 1));
    }

    @Test
    void 댓글_작성_후_사용자의_댓글_수가_증가한다() {
        // given
        Member member = getMember(1L);
        WriteCommentRequest request = new WriteCommentRequest(1L, "댓글 작성");
        Integer memberCommentCount = member.getActivityStats().getCommentCount();

        // when
        commentService.writeComment(member, request);

        // then
        assertThat(memberCommentCount + 1).isEqualTo(member.getActivityStats().getCommentCount());
    }

    @Test
    void 존재하지_않는_게시글에_댓글_작성을_하면_예외가_발생한다() {
        // given
        Member member = getMember(1L);
        WriteCommentRequest request = new WriteCommentRequest(999L, "댓글 작성");

        // when & then
        assertThatThrownBy(() -> commentService.writeComment(member, request))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.POST_NOT_FOUND);
    }

    @Test
    void 답글_작성이_성공한다() {
        // given
        Member member = getMember(1L);
        WriteReplyRequest request = new WriteReplyRequest(1L, "댓글 작성");

        // when
        NoContent result = commentService.writeReply(member, request);

        // then
        NoContent expected = NoContent.from(CommentStatusType.REPLY_CREATE_SUCCESS);
        assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    void 답글_작성_후_게시글의_댓글_수가_증가한다() {
        // given
        Member member = getMember(1L);
        WriteReplyRequest request = new WriteReplyRequest(1L, "답글 작성");
        Integer commentCount = getPost(1L).getCommentCount();

        // when
        commentService.writeReply(member, request);

        // then
        cacheClear();
        assertThat(commentCount + 1).isEqualTo(getPost(1L).getCommentCount());
    }

    @Test
    void 답글_작성_후_사용자의_댓글_수가_증가한다() {
        // given
        Member member = getMember(1L);
        WriteReplyRequest request = new WriteReplyRequest(1L, "댓글 작성");
        Integer memberCommentCount = member.getActivityStats().getCommentCount();

        // when
        commentService.writeReply(member, request);

        // then
        assertThat(memberCommentCount + 1).isEqualTo(member.getActivityStats().getCommentCount());
    }

    @Test
    void 답글_작성_후_댓글의_답글_수가_증가한다() {
        // given
        Member member = getMember(1L);
        WriteReplyRequest request = new WriteReplyRequest(1L, "댓글 작성");
        Integer replyCount = getComment(1L).getReplyCount();

        // when
        commentService.writeReply(member, request);

        // then
        cacheClear();
        assertThat(replyCount + 1).isEqualTo(getComment(1L).getReplyCount());
    }

    @Test
    void 존재하지_않는_댓글에_답글_작성을_하면_예외가_발생한다() {
        // given
        Member member = getMember(1L);
        WriteReplyRequest request = new WriteReplyRequest(999L, "댓글 작성");

        // when & then
        assertThatThrownBy(() -> commentService.writeReply(member, request))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CommentExceptionType.NOT_FOUND_COMMENT);
    }

    @Test
    void 댓글을_수정할_수_있다() {
        // given
        String content = "댓글 수정";
        Member member = getMember(1L);
        EditCommentRequest request = new EditCommentRequest(1L, content);

        // when
        commentService.updateComment(member, request);

        // then
        assertThat(getComment(1L).getContent().getValue()).isEqualTo(content);
    }

    @Test
    void 존재하지_않는_댓글을_수정하면_예외가_발생한다() {
        // given
        String content = "댓글 수정";
        Member member = getMember(1L);
        EditCommentRequest request = new EditCommentRequest(999L, content);

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(member, request))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CommentExceptionType.NOT_FOUND_COMMENT);
    }

    @Test
    void 댓글_작성자가_아닌데_댓글을_수정하면_예외가_발생한다() {
        // given
        String content = "댓글 수정";
        Member member = getMember(2L);
        EditCommentRequest request = new EditCommentRequest(1L, content);

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(member, request))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CommentExceptionType.INVALID_ACCESS);
    }

    @Test
    void 댓글_삭제가_가능하다() {
        // given
        Member member = getMember(1L);
        RemoveCommentRequest removeCommentRequest = new RemoveCommentRequest(1L);

        // when
        commentService.removeComment(member, removeCommentRequest);

        // then
        cacheClear();
        assertThat(getComment(1L).getStatus()).isEqualTo(CommentStatus.UNREGISTERED);
    }

    @Test
    void 댓글_삭제시_게시글의_댓글수가_감소한다() {
        // given
        Member member = getMember(1L);
        RemoveCommentRequest removeCommentRequest = new RemoveCommentRequest(1L);
        Integer commentCount = getPost(1L).getCommentCount();

        // when
        commentService.removeComment(member, removeCommentRequest);

        // then
        cacheClear();
        assertThat(getPost(1L).getCommentCount()).isEqualTo(commentCount - 1);
    }

    @Test
    void 답글_삭제시_댓글의_답글수가_감소한다() {
        // given
        Member member = getMember(1L);
        RemoveCommentRequest removeCommentRequest = new RemoveCommentRequest(2L);
        Integer replyCount = getComment(1L).getReplyCount();

        // when
        commentService.removeComment(member, removeCommentRequest);

        // then
        cacheClear();
        assertThat(getComment(1L).getReplyCount()).isEqualTo(replyCount - 1);
    }

    @Test
    void 존재하지_않는_댓글_삭제시_예외가_발생한다() {
        // given
        Member member = getMember(1L);
        RemoveCommentRequest removeCommentRequest = new RemoveCommentRequest(999L);

        // when & then
        assertThatThrownBy(() -> commentService.removeComment(member, removeCommentRequest))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CommentExceptionType.NOT_FOUND_COMMENT);
    }

    @Test
    void 본인이_작성하지_않은_댓글_삭제시_예외가_발생한다() {
        // given
        Member member = getMember(2L);
        RemoveCommentRequest removeCommentRequest = new RemoveCommentRequest(1L);

        // when & then
        assertThatThrownBy(() -> commentService.removeComment(member, removeCommentRequest))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", CommentExceptionType.INVALID_ACCESS);
    }

}