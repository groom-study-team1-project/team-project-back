package deepdivers.community.domain.post.repository.impl;

import static deepdivers.community.domain.hashtag.model.QHashtag.hashtag1;
import static deepdivers.community.domain.hashtag.model.QPostHashtag.postHashtag;
import static deepdivers.community.domain.image.repository.entity.QImage.image;
import static deepdivers.community.domain.member.model.QMember.member;
import static deepdivers.community.domain.post.model.QPost.post;
import static deepdivers.community.domain.post.model.like.QLike.like;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.image.domain.ImageType;
import deepdivers.community.domain.member.dto.response.AllMyPostsResponse;
import deepdivers.community.domain.post.dto.response.CountInfo;
import deepdivers.community.domain.post.dto.response.GetAllPostsResponse;
import deepdivers.community.domain.post.dto.response.MemberInfo;
import deepdivers.community.domain.post.model.vo.LikeTarget;
import deepdivers.community.domain.post.model.vo.PostStatus;
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
        final List<AllMyPostsResponse> postResponses = queryFactory.select(
                Projections.fields(
                    AllMyPostsResponse.class,
                    post.id.as("id"),
                    post.title.title.as("title"),
                    post.thumbnail.as("thumbnail"),
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
                deterMineLastContentCondition(lastContentId),
                determineCategoryCondition(categoryId),
                post.status.eq(PostStatus.ACTIVE),
                post.member.id.eq(memberId)
            )
            .orderBy(post.id.desc())
            .limit(5)
            .fetch();

        return postResponses;
    }

    private BooleanExpression hasLike(final Long memberId) {
        return post.id
            .eq(like.id.targetId)
            .and(like.id.targetType.eq(LikeTarget.POST))
            .and(like.id.memberId.eq(memberId));
    }

    @Override
    public List<GetAllPostsResponse> findAllPosts(final Long lastContentId, final Long categoryId) {
        final List<GetAllPostsResponse> allPostsResponse = queryFactory
            .select(
                Projections.fields(
                    GetAllPostsResponse.class,
                    post.id.as("postId"),
                    post.title.title.as("title"),
                    post.content.content.as("content"),
                    post.thumbnail.as("thumbnail"),
                    post.category.id.as("categoryId"),
                    post.createdAt.as("createdAt"),
                    Expressions.stringTemplate(
                        "IFNULL(GROUP_CONCAT(DISTINCT {0}), '')",
                        image.imageUrl
                    ).as("imageUrls"),
                    Expressions.stringTemplate(
                        "IFNULL(GROUP_CONCAT(DISTINCT {0}), '')",
                        hashtag1.hashtag
                    ).as("hashtags"),
                    Projections.fields(MemberInfo.class,
                        member.id.as("memberId"),
                        member.nickname.value.as("nickname"),
                        member.image.imageUrl.as("imageUrl"),
                        member.job.as("memberJob")
                    ).as("memberInfo"),
                    Projections.fields(CountInfo.class,
                        post.viewCount.as("viewCount"),
                        post.likeCount.as("likeCount"),
                        post.commentCount.as("commentCount")
                    ).as("countInfo")
                ))
            .from(post)
            .join(member).on(member.id.eq(post.member.id))
            .leftJoin(image).on(post.id.eq(image.referenceId).and(image.imageType.eq(ImageType.POST_CONTENT)))
            .leftJoin(postHashtag).on(post.id.eq(postHashtag.post.id))
            .leftJoin(hashtag1).on(postHashtag.hashtag.id.eq(hashtag1.id))
            .where(
                deterMineLastContentCondition(lastContentId),
                determineCategoryCondition(categoryId),
                post.status.eq(PostStatus.ACTIVE)
            )
            .groupBy(
                post.id,
                post.title.title,
                post.content.content,
                post.category.id,
                member.id,
                member.nickname.value,
                member.image.imageUrl,
                member.job,
                post.viewCount,
                post.likeCount,
                post.commentCount,
                post.createdAt
            )
            .orderBy(post.id.desc())
            .limit(10)
            .fetch();

        return allPostsResponse;
    }

    private static Predicate deterMineLastContentCondition(final Long lastContentId) {
        if (lastContentId == null) {
            return null;
        }
        return post.id.lt(lastContentId);
    }

    private static Predicate determineCategoryCondition(final Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return post.category.id.eq(categoryId);
    }

}
