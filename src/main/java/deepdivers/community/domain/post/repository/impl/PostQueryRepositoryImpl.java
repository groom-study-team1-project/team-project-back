package deepdivers.community.domain.post.repository.impl;

import static deepdivers.community.domain.member.model.QMember.member;
import static deepdivers.community.domain.post.model.QPost.post;
import static deepdivers.community.domain.post.model.like.QLike.like;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.hashtag.application.interfaces.HashtagQueryRepository;
import deepdivers.community.domain.image.application.interfaces.ImageQueryRepository;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.vo.LikeTarget;
import deepdivers.community.domain.post.model.vo.PostStatus;
import deepdivers.community.domain.post.repository.PostQueryRepository;
import deepdivers.community.domain.post.repository.generator.PostQBeanGenerator;
import deepdivers.community.global.exception.model.NotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final HashtagQueryRepository hashtagQueryRepository;
    private final ImageQueryRepository imageQueryRepository;

    // todo: 몇개 씩 조회할 지 정하기
    // todo: 정렬 순서 정하기
    @Override
    public List<PostPreviewResponse> findAllPosts(final Long memberId, final Long lastPostId, final Long categoryId) {
        final List<PostPreviewResponse> postPreviewResponses = extractPostPreview(memberId, lastPostId, categoryId);
        final List<Long> postIds = postPreviewResponses.stream().map(PostPreviewResponse::getPostId).toList();
        final Map<Long, List<String>> hashtagsByPostId = hashtagQueryRepository.findAllHashtagByPosts(postIds);

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
            throw new NotFoundException(PostExceptionType.POST_NOT_FOUND);
        }

        postDetailResponse.setHashtags(hashtagQueryRepository.findAllHashtagByPost(postId));
        postDetailResponse.setImageUrls(imageQueryRepository.findAllImagesByPost(postId));

        return postDetailResponse;
    }

    private List<PostPreviewResponse> extractPostPreview(
        final Long memberId,
        final Long lastPostId,
        final Long categoryId
    ) {
        return queryFactory.select(PostQBeanGenerator.createPostPreview(post, member))
            .from(post)
            .join(member).on(member.id.eq(post.member.id))
            .where(
                determineAuthorCheckingCondition(memberId),
                deterMineLastContentCondition(lastPostId),
                determineCategoryCondition(categoryId),
                post.status.eq(PostStatus.ACTIVE)
            )
            .orderBy(post.id.desc())
            .limit(10)
            .fetch();
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
        if (memberId == 0L) {
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
