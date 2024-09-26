package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.WriteCommentRequest;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.comment.Comment;
import deepdivers.community.domain.post.repository.CommentRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public NoContent writeComment(final Member member, final WriteCommentRequest request) {
        final Post post = postRepository.findById(request.postId())
            .orElseThrow(IllegalArgumentException::new);

        final Comment comment = Comment.of(post, member, request.content());
        commentRepository.save(comment);

        return NoContent.from(PostStatusType.POST_VIEW_SUCCESS);
    }

}
