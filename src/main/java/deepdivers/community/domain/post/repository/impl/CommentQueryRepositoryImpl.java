package deepdivers.community.domain.post.repository.impl;

import static deepdivers.community.domain.member.model.QMember.member;
import static deepdivers.community.domain.post.model.QPost.post;
import static deepdivers.community.domain.post.model.comment.QComment.comment;
import static deepdivers.community.domain.post.model.like.QLike.like;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.post.dto.response.CommentResponse;
import deepdivers.community.domain.post.dto.response.GetCommentResponse;
import deepdivers.community.domain.post.model.vo.CommentStatus;
import deepdivers.community.domain.post.model.vo.LikeTarget;
import deepdivers.community.domain.post.repository.CommentQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GetCommentResponse> findTop5CommentsByPost(
        final Long postId,
        final Long memberId,
        final Long lastCommentId
    ) {
        return queryFactory.select(
                Projections.fields(
                    GetCommentResponse.class,
                    comment.id.as("id"),
                    comment.content.value.as("content"),
                    comment.replyCount.as("replyCount"),
                    comment.likeCount.as("likeCount"),
                    comment.createdAt.as("createdAt"),
                    member.id.as("memberId"),
                    member.nickname.value.as("memberNickname"),
                    member.imageUrl.as("memberImageUrl"),
                    comment.createdAt.ne(comment.updatedAt).as("isModified"),
                    like.isNotNull().as("isLikedMe")
                ))
            .from(comment)
            .join(member).on(member.id.eq(comment.member.id))
            .leftJoin(like).on(hasLike(memberId))
            .where(
                comment.post.id.eq(postId),
                comment.id.lt(lastCommentId),
                comment.parentCommentId.isNull(),
                comment.status.eq(CommentStatus.REGISTERED)
            )
            .orderBy(comment.id.desc())
            .limit(5)
            .fetch();
    }

    private BooleanExpression hasLike(final Long memberId) {
        return comment.id
            .eq(like.id.targetId)
            .and(like.id.targetType.eq(LikeTarget.COMMENT))
            .and(like.id.memberId.eq(memberId));
    }

}
