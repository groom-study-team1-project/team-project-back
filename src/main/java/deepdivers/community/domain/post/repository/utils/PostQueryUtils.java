package deepdivers.community.domain.post.repository.utils;

import static com.querydsl.core.types.ExpressionUtils.and;
import static deepdivers.community.domain.like.entity.QLike.like;
import static deepdivers.community.domain.post.entity.QPost.post;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import deepdivers.community.domain.category.entity.CategoryType;
import deepdivers.community.domain.like.entity.LikeTarget;
import deepdivers.community.domain.post.entity.PostSortType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostQueryUtils {

    private static final int GET_POSTS_DEFAULT_LIMIT_COUNT = 5;
    private static final int GET_POSTS_MAX_LIMIT_COUNT = 30;

    public static int getLimitOrDefault(final Integer limit) {
        if (limit == null || limit <= GET_POSTS_DEFAULT_LIMIT_COUNT) {
            return GET_POSTS_DEFAULT_LIMIT_COUNT;
        }
        if (limit > GET_POSTS_MAX_LIMIT_COUNT) {
            return GET_POSTS_MAX_LIMIT_COUNT;
        }
        return limit;
    }

    public static OrderSpecifier<?>[] determinePostSortCondition(final PostSortType sortType) {
        return switch (sortType) {
            case HOT -> new OrderSpecifier<?>[]{ post.viewCount.desc(), post.id.desc() };
            case COMMENT -> new OrderSpecifier<?>[]{ post.commentCount.desc(), post.id.desc() };
            case null, default -> new OrderSpecifier<?>[]{ post.id.desc() };
        };
    }

    public static Predicate deterMineLastContentCondition(final Long lastContentId) {
        if (lastContentId == null) {
            return null;
        }
        return post.id.lt(lastContentId);
    }

    public static BooleanExpression determineAuthorCheckingCondition(final Long memberId) {
        if (memberId == null) {
            return null;
        }
        return post.member.id.eq(memberId);
    }

    public static Predicate determineCategoryCondition(final Long categoryId, final CategoryType categoryType) {
        if (categoryId == null) {
            return post.category.categoryType.eq(categoryType);
        }
        return and(post.category.id.eq(categoryId), post.category.categoryType.eq(categoryType));
    }

    public static BooleanExpression hasLike(final Long memberId) {
        return post.id
            .eq(like.id.targetId)
            .and(like.id.targetType.eq(LikeTarget.POST))
            .and(like.id.memberId.eq(memberId));
    }

}
