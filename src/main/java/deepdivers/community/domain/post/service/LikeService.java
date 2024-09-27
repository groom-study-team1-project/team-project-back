package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.dto.response.statustype.LikeStatusType;
import deepdivers.community.domain.post.exception.LikeExceptionType;
import deepdivers.community.domain.post.model.like.Like;
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
        likeRepository.findById(like.getId())
            .ifPresent(liked -> {throw new BadRequestException(LikeExceptionType.INVALID_ACCESS);});

        likeRepository.save(like);
        commentRepository.incrementLikeCount(request.targetId());

        return NoContent.from(LikeStatusType.COMMENT_LIKE_SUCCESS);
    }

    public NoContent unlikeComment(final LikeRequest request, final Long memberId)  {
        final Like like = Like.of(request.targetId(), memberId, LikeTarget.COMMENT);
        if (!likeRepository.existsById(like.getId())) {
            throw new BadRequestException(LikeExceptionType.INVALID_ACCESS);
        }

        likeRepository.delete(like);
        commentRepository.decrementLikeCount(request.targetId());

        return NoContent.from(LikeStatusType.COMMENT_UNLIKE_SUCCESS);
    }

}
