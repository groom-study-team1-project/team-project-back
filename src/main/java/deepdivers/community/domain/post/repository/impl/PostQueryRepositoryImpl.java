package deepdivers.community.domain.post.repository.impl;

import static deepdivers.community.domain.member.model.QMember.member;
import static deepdivers.community.domain.post.model.QPost.post;
import static deepdivers.community.domain.post.model.like.QLike.like;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.member.dto.response.AllMyPostsResponse;
import deepdivers.community.domain.post.model.QPost;
import deepdivers.community.domain.post.model.vo.LikeTarget;
import deepdivers.community.domain.post.repository.PostQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AllMyPostsResponse> findAllMyPosts(
        final Long memberId,
        final Long lastContentId,
        final Long categoryId
    ) {
        return queryFactory.select(
            Projections.fields(
                AllMyPostsResponse.class,
                post.id.as("id"),
                post.title.as("title"),
                post.viewCount.as("viewCount"),
                post.likeCount.as("likeCount"),
                post.commentCount.as("commentCount"),
                post.createdAt.as("createdAt"),
                member.id.as("memberId"),
                member.nickname.value.as("memberNickname"),
                like.isNotNull().as("isLikedMe")
            ))
            .from(post)
            .join(member).on(post.member.id.eq(member.id))
            .leftJoin(like).on(hasLike(memberId))
            .where(
                post.category.id.eq(categoryId),
                post.member.id.eq(memberId),
                post.id.lt(lastContentId)
            )
            .orderBy(post.id.desc())
            .limit(10)
            .fetch();
    }

    private BooleanExpression hasLike(final Long memberId) {
        return post.id
            .eq(like.id.targetId)
            .and(like.id.targetType.eq(LikeTarget.POST))
            .and(like.id.memberId.eq(memberId));
    }

}
