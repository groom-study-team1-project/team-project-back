package deepdivers.community.domain.comment.service;

import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.comment.dto.request.EditCommentRequest;
import deepdivers.community.domain.comment.dto.request.RemoveCommentRequest;
import deepdivers.community.domain.comment.dto.request.WriteCommentRequest;
import deepdivers.community.domain.comment.dto.request.WriteReplyRequest;
import deepdivers.community.domain.comment.dto.code.CommentStatusCode;
import deepdivers.community.domain.comment.exception.CommentExceptionCode;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import deepdivers.community.domain.post.entity.Post;
import deepdivers.community.domain.comment.entity.Comment;
import deepdivers.community.domain.comment.entity.CommentStatus;
import deepdivers.community.domain.comment.repository.jpa.CommentRepository;
import deepdivers.community.domain.post.repository.jpa.PostRepository;
import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.common.exception.NotFoundException;
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
        final Post post = getPostWithoutException(request);
        final Comment comment = Comment.of(post, member, request.content());

        member.incrementCommentCount();
        postRepository.incrementCommentCount(post.getId());
        commentRepository.save(comment);

        return NoContent.from(CommentStatusCode.COMMENT_CREATE_SUCCESS);
    }

    private Post getPostWithoutException(WriteCommentRequest request) {
        return postRepository.findById(request.postId())
            .orElseThrow(() -> new NotFoundException(PostExceptionCode.POST_NOT_FOUND));
    }

    public NoContent writeReply(final Member member, final WriteReplyRequest request) {
        final Comment comment = getCommentWithThrow(request.commentId());
        final Comment reply = Comment.of(comment.getPost(), member, request);

        member.incrementCommentCount();
        postRepository.incrementCommentCount(comment.getPost().getId());
        commentRepository.incrementReplyCount(reply.getParentCommentId());
        commentRepository.save(reply);

        return NoContent.from(CommentStatusCode.REPLY_CREATE_SUCCESS);
    }

    public NoContent updateComment(final Member member, final EditCommentRequest request) {
        final Comment comment = getCommentWithThrow(request.commentId());
        validateAuthor(member, comment.getMember());

        comment.updateComment(request.content());
        commentRepository.save(comment);

        return NoContent.from(CommentStatusCode.COMMENT_EDIT_SUCCESS);
    }

    private void validateAuthor(final Member member, final Member author) {
        if (!member.equals(author)) {
            throw new BadRequestException(CommentExceptionCode.INVALID_ACCESS);
        }
    }

    private Comment getCommentWithThrow(final Long id) {
        return commentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(CommentExceptionCode.NOT_FOUND_COMMENT));
    }

    public NoContent removeComment(final Member member, final RemoveCommentRequest request) {
        final Comment comment = getCommentWithThrow(request.commentId());
        validateAuthor(member, comment.getMember());

        commentRepository.deleteComment(comment.getId(), CommentStatus.UNREGISTERED);
        postRepository.decrementCommentCount(comment.getPost().getId());
        decrementReplyCount(comment);

        return NoContent.from(CommentStatusCode.COMMENT_REMOVE_SUCCESS);
    }

    private void decrementReplyCount(final Comment comment) {
        if (comment.getParentCommentId() != null) {
            commentRepository.decrementReplyCount(comment.getParentCommentId());
        }
    }

}
