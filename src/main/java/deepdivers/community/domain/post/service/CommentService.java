package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.EditCommentRequest;
import deepdivers.community.domain.post.dto.request.RemoveCommentRequest;
import deepdivers.community.domain.post.dto.request.WriteCommentRequest;
import deepdivers.community.domain.post.dto.request.WriteReplyRequest;
import deepdivers.community.domain.post.dto.response.statustype.CommentStatusType;
import deepdivers.community.domain.post.exception.CommentExceptionType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.comment.Comment;
import deepdivers.community.domain.post.repository.CommentRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public NoContent writeComment(final Member member, final WriteCommentRequest request) {
        final Post post = postRepository.findById(request.postId())
            .orElseThrow(() -> new NotFoundException(PostExceptionType.POST_NOT_FOUND));
        final Comment comment = Comment.of(post, member, request.content());

        commentRepository.save(comment);
        postRepository.incrementCommentCount(post.getId());

        return NoContent.from(CommentStatusType.COMMENT_CREATE_SUCCESS);
    }

    public NoContent writeReply(final Member member, final WriteReplyRequest request) {
        final Comment comment = getCommentWithThrow(request.commentId());
        final Comment reply = Comment.of(comment.getPost(), member, request);

        commentRepository.save(reply);
        postRepository.incrementCommentCount(comment.getPost().getId());
        commentRepository.incrementReplyCount(reply.getParentCommentId());

        return NoContent.from(CommentStatusType.REPLY_CREATE_SUCCESS);
    }

    public NoContent updateComment(final Member member, final EditCommentRequest request) {
        final Comment comment = getCommentWithThrow(request.commentId());
        validateAuthor(member, comment.getMember());

        comment.updateComment(request.content());
        commentRepository.save(comment);

        return NoContent.from(CommentStatusType.COMMENT_EDIT_SUCCESS);
    }

    private void validateAuthor(final Member member, final Member author) {
        if (!member.equals(author)) {
            throw new BadRequestException(CommentExceptionType.NOT_FOUND_COMMENT);
        }
    }

    private Comment getCommentWithThrow(final Long id) {
        return commentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(PostExceptionType.POST_NOT_FOUND));
    }

    public NoContent removeComment(final Member member, final RemoveCommentRequest request) {
        // todo: 댓글 삭제 시, 게시글 댓글 수 감소 & 답변일 경우 댓글 답글 수 감소
        final Comment comment = getCommentWithThrow(request.commentId());
        validateAuthor(member, comment.getMember());

        comment.deleteComment();
        commentRepository.save(comment);

        return NoContent.from(CommentStatusType.COMMENT_REMOVE_SUCCESS);
    }

}
