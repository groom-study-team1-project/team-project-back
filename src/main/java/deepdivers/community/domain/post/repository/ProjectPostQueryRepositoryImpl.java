package deepdivers.community.domain.post.repository;

import static com.querydsl.core.types.ExpressionUtils.and;
import static deepdivers.community.domain.file.repository.entity.QFile.file;
import static deepdivers.community.domain.hashtag.entity.QHashtag.hashtag1;
import static deepdivers.community.domain.hashtag.entity.QPostHashtag.postHashtag;
import static deepdivers.community.domain.member.entity.QMember.member;
import static deepdivers.community.domain.post.entity.QPost.post;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.category.entity.CategoryType;
import deepdivers.community.domain.file.repository.entity.FileType;
import deepdivers.community.domain.post.controller.interfaces.ProjectPostQueryRepository;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPreviewResponse;
import deepdivers.community.domain.post.entity.PostSortType;
import deepdivers.community.domain.post.entity.PostStatus;
import deepdivers.community.domain.post.repository.generator.PostQBeanGenerator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectPostQueryRepositoryImpl implements ProjectPostQueryRepository {

    private static final int GET_POSTS_DEFAULT_LIMIT_COUNT = 5;
    private static final int GET_POSTS_MAX_LIMIT_COUNT = 30;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProjectPostPreviewResponse> findAllPosts(Long memberId, GetPostsRequest dto) {
        final List<ProjectPostPreviewResponse> postPreviewResponses = extractPostPreview(memberId, dto);
        final List<Long> postIds = postPreviewResponses.stream().map(PostPreviewResponse::getPostId).toList();
        final Map<Long, List<String>> hashtagsByPosts = findAllHashtagByPosts(postIds);
        final Map<Long, List<String>> slideImageUrlByPosts = findAllSlideImageUrlByPosts(postIds);

        postPreviewResponses.forEach(postPreviewResponse -> {
            final Long postId = postPreviewResponse.getPostId();
            postPreviewResponse.setHashtags(hashtagsByPosts.getOrDefault(postId, Collections.emptyList()));
            postPreviewResponse.setSlideImageUrls(slideImageUrlByPosts.getOrDefault(postId, Collections.emptyList()));
        });

        return postPreviewResponses;
    }

    @Override
    public ProjectPostDetailResponse readPostByPostId(Long postId, Long viewerId) {
        return null;
    }

    private Map<Long, List<String>> findAllSlideImageUrlByPosts(List<Long> postIds) {
        return queryFactory.select(post.id, file.fileUrl)
            .from(post)
            .leftJoin(file).on(file.referenceId.eq(post.id))
            .where(post.id.in(postIds), file.fileType.eq(FileType.POST_SLIDE))
            .transform(GroupBy.groupBy(post.id)
                .as(GroupBy.list(file.fileUrl)));
    }

    private List<ProjectPostPreviewResponse> extractPostPreview(final Long memberId, final GetPostsRequest dto) {
        return queryFactory.select(PostQBeanGenerator.createPreview(ProjectPostPreviewResponse.class, post, member))
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

    private Map<Long, List<String>> findAllHashtagByPosts(final List<Long> postIds) {
        return queryFactory.select(post.id, hashtag1.hashtag)
            .from(post)
            .leftJoin(postHashtag).on(post.id.eq(postHashtag.post.id))
            .leftJoin(hashtag1).on(postHashtag.hashtag.id.eq(hashtag1.id))
            .where(post.id.in(postIds))
            .transform(GroupBy.groupBy(post.id)
                .as(GroupBy.list(hashtag1.hashtag)));
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
            return post.category.categoryType.eq(CategoryType.PROJECT);
        }
        return and(post.category.id.eq(categoryId), post.category.categoryType.eq(CategoryType.PROJECT));
    }

    private static BooleanExpression determineAuthorCheckingCondition(final Long memberId) {
        if (memberId == null) {
            return null;
        }
        return post.member.id.eq(memberId);
    }

}
