package deepdivers.community.domain.post.repository;

import static deepdivers.community.domain.member.entity.QMember.member;
import static deepdivers.community.domain.post.entity.comment.QComment.comment;
import static deepdivers.community.domain.post.entity.like.QLike.like;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.post.controller.interfaces.CommentQueryRepository;
import deepdivers.community.domain.post.dto.response.ContentResponse;
import deepdivers.community.domain.post.dto.response.GetCommentResponse;
import deepdivers.community.domain.post.entity.comment.CommentStatus;
import deepdivers.community.domain.post.entity.like.LikeTarget;
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
                    member.image.imageUrl.as("memberImageUrl"),
                    comment.createdAt.ne(comment.updatedAt).as("isModified"),
                    like.isNotNull().as("isLikedMe"),
                    comment.member.id.eq(memberId).as("isAuthor")
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

    @Override
    public List<ContentResponse> findTop5RepliesByComment(
        final Long commentId,
        final Long memberId,
        final Long lastCommentId
    ) {
        return queryFactory.select(
                Projections.fields(
                    ContentResponse.class,
                    comment.id.as("id"),
                    comment.content.value.as("content"),
                    comment.likeCount.as("likeCount"),
                    comment.createdAt.as("createdAt"),
                    member.id.as("memberId"),
                    member.nickname.value.as("memberNickname"),
                    member.image.imageUrl.as("memberImageUrl"),
                    comment.createdAt.ne(comment.updatedAt).as("isModified"),
                    like.isNotNull().as("isLikedMe"),
                    comment.member.id.eq(memberId).as("isAuthor")
                ))
            .from(comment)
            .join(member).on(member.id.eq(comment.member.id))
            .leftJoin(like).on(hasLike(memberId))
            .where(
                comment.parentCommentId.eq(commentId),
                comment.id.lt(lastCommentId),
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
