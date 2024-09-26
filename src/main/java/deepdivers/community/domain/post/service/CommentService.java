package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.WriteCommentRequest;
import deepdivers.community.domain.post.dto.request.WriteReplyRequest;
import deepdivers.community.domain.post.dto.response.statustype.CommentStatusType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.comment.Comment;
import deepdivers.community.domain.post.repository.CommentRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
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
            .orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));

        final Comment comment = Comment.of(post, member, request.content());
        commentRepository.save(comment);
        postRepository.incrementCommentCount(post.getId());

        return NoContent.from(CommentStatusType.COMMENT_CREATE_SUCCESS);
    }

    public NoContent writeReply(final Member member, final WriteReplyRequest request) {
        final Comment comment = commentRepository.findById(request.commentId())
            .orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));

        final Comment reply = Comment.of(comment.getPost(), member, request.content());
        commentRepository.save(reply);
        postRepository.incrementCommentCount(comment.getPost().getId());

        return NoContent.from(CommentStatusType.REPLY_CREATE_SUCCESS);
    }

}
