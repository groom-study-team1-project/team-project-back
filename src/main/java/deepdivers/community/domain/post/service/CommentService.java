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
import deepdivers.community.domain.post.model.vo.CommentStatus;
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
        final Post post = getPostWithoutException(request);
        final Comment comment = Comment.of(post, member, request.content());

        member.incrementCommentCount();
        postRepository.incrementCommentCount(post.getId());
        commentRepository.save(comment);

        return NoContent.from(CommentStatusType.COMMENT_CREATE_SUCCESS);
    }

    private Post getPostWithoutException(WriteCommentRequest request) {
        return postRepository.findById(request.postId())
            .orElseThrow(() -> new NotFoundException(PostExceptionType.POST_NOT_FOUND));
    }

    public NoContent writeReply(final Member member, final WriteReplyRequest request) {
        final Comment comment = getCommentWithThrow(request.commentId());
        final Comment reply = Comment.of(comment.getPost(), member, request);

        member.incrementCommentCount();
        postRepository.incrementCommentCount(comment.getPost().getId());
        commentRepository.incrementReplyCount(reply.getParentCommentId());
        commentRepository.save(reply);

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
            throw new BadRequestException(CommentExceptionType.INVALID_ACCESS);
        }
    }

    private Comment getCommentWithThrow(final Long id) {
        return commentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(CommentExceptionType.NOT_FOUND_COMMENT));
    }

    public NoContent removeComment(final Member member, final RemoveCommentRequest request) {
        final Comment comment = getCommentWithThrow(request.commentId());
        validateAuthor(member, comment.getMember());

        commentRepository.deleteComment(comment.getId(), CommentStatus.UNREGISTERED);
        postRepository.decrementCommentCount(comment.getPost().getId());
        decrementReplyCount(comment);

        return NoContent.from(CommentStatusType.COMMENT_REMOVE_SUCCESS);
    }

    private void decrementReplyCount(final Comment comment) {
        if (comment.getParentCommentId() != null) {
            commentRepository.decrementReplyCount(comment.getParentCommentId());
        }
    }

}
