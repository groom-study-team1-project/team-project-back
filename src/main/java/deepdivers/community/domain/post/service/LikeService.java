package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.dto.response.statustype.CommentStatusType;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.LikeExceptionType;
import deepdivers.community.domain.post.model.like.Like;
import deepdivers.community.domain.post.model.like.LikeId;
import deepdivers.community.domain.post.model.vo.LikeTarget;
import deepdivers.community.domain.post.repository.CommentRepository;
import deepdivers.community.domain.post.repository.LikeRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public NoContent likeComment(final LikeRequest request, final Long memberId)  {
        final Like like = Like.of(request.targetId(), memberId, LikeTarget.COMMENT);
        validateExistsLike(like.getId());

        likeRepository.save(like);
        commentRepository.incrementLikeCount(request.targetId());

        return NoContent.from(CommentStatusType.COMMENT_LIKE_SUCCESS);
    }

    public NoContent unlikeComment(final LikeRequest request, final Long memberId)  {
        final Like like = Like.of(request.targetId(), memberId, LikeTarget.COMMENT);
        validateNotExistsLike(like.getId());

        likeRepository.delete(like);
        commentRepository.decrementLikeCount(request.targetId());

        return NoContent.from(CommentStatusType.COMMENT_UNLIKE_SUCCESS);
    }

    public NoContent likePost(final LikeRequest request, final Long memberId)  {
        final Like like = Like.of(request.targetId(), memberId, LikeTarget.POST);
        validateExistsLike(like.getId());

        likeRepository.save(like);
        postRepository.incrementLikeCount(request.targetId());

        return NoContent.from(PostStatusType.POST_LIKE_SUCCESS);
    }

    public NoContent unlikePost(final LikeRequest request, final Long memberId)  {
        final Like like = Like.of(request.targetId(), memberId, LikeTarget.POST);
        validateNotExistsLike(like.getId());

        likeRepository.delete(like);
        postRepository.decrementLikeCount(request.targetId());

        return NoContent.from(PostStatusType.POST_UNLIKE_SUCCESS);
    }

    private void validateExistsLike(final LikeId likeId) {
        if (likeRepository.existsById(likeId)) {
            throw new BadRequestException(LikeExceptionType.INVALID_ACCESS);
        }
    }

    private void validateNotExistsLike(final LikeId likeId) {
        if (!likeRepository.existsById(likeId)) {
            throw new BadRequestException(LikeExceptionType.INVALID_ACCESS);
        }
    }

}