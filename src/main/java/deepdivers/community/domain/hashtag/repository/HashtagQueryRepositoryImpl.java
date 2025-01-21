package deepdivers.community.domain.hashtag.repository;

import static deepdivers.community.domain.category.entity.QPostCategory.postCategory;
import static deepdivers.community.domain.hashtag.entity.QHashtag.hashtag1;
import static deepdivers.community.domain.hashtag.entity.QPostHashtag.postHashtag;
import static deepdivers.community.domain.post.entity.QPost.post;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.category.entity.CategoryType;
import deepdivers.community.domain.hashtag.controller.interfaces.HashtagQueryRepository;
import deepdivers.community.domain.hashtag.dto.PopularHashtagResponse;
import deepdivers.community.domain.post.entity.PostStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HashtagQueryRepositoryImpl implements HashtagQueryRepository {

    private static final int WEEKLY_BASE_DAY = 7;

    private final JPAQueryFactory queryFactory;

    public List<String> findAllHashtagByPost(final Long postId) {
        return queryFactory
            .select(hashtag1.hashtag)
            .from(postHashtag)
            .leftJoin(hashtag1).on(postHashtag.hashtag.id.eq(hashtag1.id))
            .where(postHashtag.post.id.eq(postId))
            .fetch();
    }

    @Override
    public Map<Long, List<String>> findAllHashtagByPosts(List<Long> postIds) {
        return queryFactory.select(post.id, hashtag1.hashtag)
            .from(post)
            .leftJoin(postHashtag).on(post.id.eq(postHashtag.post.id))
            .leftJoin(hashtag1).on(postHashtag.hashtag.id.eq(hashtag1.id))
            .where(post.id.in(postIds))
            .transform(GroupBy.groupBy(post.id)
                .as(GroupBy.list(hashtag1.hashtag)));
    }

    @Override
    public List<PopularHashtagResponse> findWeeklyPopularHashtagByCategory(
        final Long categoryId,
        final CategoryType categoryType
    ) {
        return queryFactory
            .select(Projections.fields(
                PopularHashtagResponse.class,
                postHashtag.hashtag.id.as("hashtagId"),
                postHashtag.hashtag.hashtag.as("hashtagName")))
            .from(postHashtag)
            .join(hashtag1).on(hashtag1.id.eq(postHashtag.hashtag.id))
            .join(postHashtag.post, post)
            .join(post.category, postCategory)
            .where(
                postHashtag.createdAt.after(LocalDateTime.now().minusDays(WEEKLY_BASE_DAY)),
                post.status.eq(PostStatus.ACTIVE),
                postCategory.id.eq(categoryId),
                postCategory.categoryType.eq(categoryType)
            )
            .groupBy(postHashtag.hashtag.id)
            .orderBy(postHashtag.count().desc())
            .limit(5)
            .fetch();
    }

}
