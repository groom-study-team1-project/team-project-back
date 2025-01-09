package deepdivers.community.domain.post.repository;

import static deepdivers.community.domain.hashtag.entity.QHashtag.hashtag1;
import static deepdivers.community.domain.hashtag.entity.QPostHashtag.postHashtag;
import static deepdivers.community.domain.like.entity.QLike.like;
import static deepdivers.community.domain.member.entity.QMember.member;
import static deepdivers.community.domain.post.entity.QPost.post;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.hashtag.controller.interfaces.HashtagQueryRepository;
import deepdivers.community.domain.image.application.interfaces.ImageQueryRepository;
import deepdivers.community.domain.post.controller.interfaces.PostQueryRepository;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import deepdivers.community.domain.like.entity.LikeTarget;
import deepdivers.community.domain.post.entity.PostSortType;
import deepdivers.community.domain.post.entity.PostStatus;
import deepdivers.community.domain.post.repository.generator.PostQBeanGenerator;
import deepdivers.community.domain.common.exception.NotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private static final int GET_POSTS_DEFAULT_LIMIT_COUNT = 5;
    private static final int GET_POSTS_MAX_LIMIT_COUNT = 30;

    private final JPAQueryFactory queryFactory;
    private final HashtagQueryRepository hashtagQueryRepository;
    private final ImageQueryRepository imageQueryRepository;

    @Override
    public List<PostPreviewResponse> findAllPosts(final Long memberId, final GetPostsRequest dto) {
        final List<PostPreviewResponse> postPreviewResponses = extractPostPreview(memberId, dto);
        final List<Long> postIds = postPreviewResponses.stream().map(PostPreviewResponse::getPostId).toList();
        final Map<Long, List<String>> hashtagsByPostId = findAllHashtagByPosts(postIds);

        postPreviewResponses.forEach(postPreviewResponse -> {
            final List<String> hashtags = hashtagsByPostId.getOrDefault(
                postPreviewResponse.getPostId(),
                Collections.emptyList()
            );
            postPreviewResponse.setHashtags(hashtags);
        });

        return postPreviewResponses;
    }

    @Override
    public PostDetailResponse readPostByPostId(final Long postId, final Long viewerId) {
        final PostDetailResponse postDetailResponse = queryFactory
            .select(PostQBeanGenerator.createPostDetail(post, member, like, viewerId))
            .from(post)
            .join(member).on(post.member.id.eq(member.id))
            .leftJoin(like).on(hasLike(viewerId))
            .where(post.id.eq(postId))
            .fetchOne();

        if (postDetailResponse == null) {
            throw new NotFoundException(PostExceptionCode.POST_NOT_FOUND);
        }

        postDetailResponse.setHashtags(hashtagQueryRepository.findAllHashtagByPost(postId));
        postDetailResponse.setImageUrls(imageQueryRepository.findAllImageUrlsByPost(postId));

        return postDetailResponse;
    }

    private Map<Long, List<String>> findAllHashtagByPosts(final List<Long> postIds) {
        return queryFactory.select(post.id, hashtag1.hashtag)
            .from(post)
            .leftJoin(postHashtag).on(post.id.eq(postHashtag.post.id))
            .leftJoin(hashtag1).on(postHashtag.hashtag.id.eq(hashtag1.id))
            .where(post.id.in(postIds))
            .transform(GroupBy.groupBy(post.id)
                .as(GroupBy.list(hashtag1.hashtag)));
    }

    private List<PostPreviewResponse> extractPostPreview(final Long memberId, final GetPostsRequest dto) {
        return queryFactory.select(PostQBeanGenerator.createPostPreview(post, member))
            .from(post)
            .join(member).on(member.id.eq(post.member.id))
            .where(
                determineAuthorCheckingCondition(memberId),
                deterMineLastContentCondition(dto.lastPostId()),
                determineCategoryCondition(dto.categoryId()),
                post.status.eq(PostStatus.ACTIVE)
            )
            .orderBy(determinePostSortCondition(dto.postSortType()))
            .limit(getLimitOrDefault(dto.limit()))
            .fetch();
    }

    private static int getLimitOrDefault(final Integer limit) {
        if (limit == null || limit <= GET_POSTS_DEFAULT_LIMIT_COUNT) {
            return GET_POSTS_DEFAULT_LIMIT_COUNT;
        }
        if (limit > GET_POSTS_MAX_LIMIT_COUNT) {
            return GET_POSTS_MAX_LIMIT_COUNT;
        }
        return limit;
    }

    private static OrderSpecifier<?>[] determinePostSortCondition(final PostSortType sortType) {
        return switch (sortType) {
            case HOT -> new OrderSpecifier<?>[]{ post.viewCount.desc(), post.id.desc() };
            case COMMENT -> new OrderSpecifier<?>[]{ post.commentCount.desc(), post.id.desc() };
            case null, default -> new OrderSpecifier<?>[]{ post.id.desc() };
        };
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

    private static BooleanExpression determineAuthorCheckingCondition(final Long memberId) {
        if (memberId == null) {
            return null;
        }
        return post.member.id.eq(memberId);
    }

    private BooleanExpression hasLike(final Long memberId) {
        if (memberId == null) {
            return null;
        }
        return post.id
            .eq(like.id.targetId)
            .and(like.id.targetType.eq(LikeTarget.POST))
            .and(like.id.memberId.eq(memberId));
    }

}
