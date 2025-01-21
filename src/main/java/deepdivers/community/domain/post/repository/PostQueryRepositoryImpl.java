package deepdivers.community.domain.post.repository;

import static deepdivers.community.domain.category.entity.QPostCategory.postCategory;
import static deepdivers.community.domain.like.entity.QLike.like;
import static deepdivers.community.domain.member.entity.QMember.member;
import static deepdivers.community.domain.post.entity.QPost.post;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.category.entity.CategoryType;
import deepdivers.community.domain.common.exception.NotFoundException;
import deepdivers.community.domain.file.application.interfaces.FileQueryRepository;
import deepdivers.community.domain.file.repository.entity.FileType;
import deepdivers.community.domain.hashtag.controller.interfaces.HashtagQueryRepository;
import deepdivers.community.domain.hashtag.dto.PopularHashtagResponse;
import deepdivers.community.domain.post.controller.interfaces.PostQueryRepository;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.NormalPostPageResponse;
import deepdivers.community.domain.post.dto.response.PopularPostResponse;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.entity.PostStatus;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import deepdivers.community.domain.post.repository.utils.PostQBeanGenerator;
import deepdivers.community.domain.post.repository.utils.PostQueryUtils;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private static final int WEEKLY_BASE_DAY = 7;

    private final JPAQueryFactory queryFactory;
    private final HashtagQueryRepository hashtagQueryRepository;
    private final FileQueryRepository fileQueryRepository;

    @Override
    public List<PostPreviewResponse> findAllPosts(final Long memberId, final GetPostsRequest dto) {
        final List<PostPreviewResponse> postPreviewResponses = extractPostPreview(memberId, dto);
        final List<Long> postIds = postPreviewResponses.stream().map(PostPreviewResponse::getPostId).toList();
        final Map<Long, List<String>> hashtagsByPosts = hashtagQueryRepository.findAllHashtagByPosts(postIds);

        postPreviewResponses.forEach(postPreviewResponse -> {
            final Long postId = postPreviewResponse.getPostId();
            postPreviewResponse.setHashtags(hashtagsByPosts.getOrDefault(postId, Collections.emptyList()));
        });

        return postPreviewResponses;
    }

    @Override
    public PostDetailResponse readPostByPostId(final Long postId, final Long viewerId) {
        final PostDetailResponse postDetailResponse = queryFactory
            .select(PostQBeanGenerator.createPostDetail(PostDetailResponse.class, viewerId))
            .from(post)
            .join(member).on(post.member.id.eq(member.id))
            .leftJoin(like).on(PostQueryUtils.hasLike(viewerId))
            .where(post.id.eq(postId))
            .fetchOne();

        if (postDetailResponse == null) {
            throw new NotFoundException(PostExceptionCode.POST_NOT_FOUND);
        }

        postDetailResponse.setHashtags(hashtagQueryRepository.findAllHashtagByPost(postId));
        postDetailResponse.setImageUrls(fileQueryRepository.findAllImageUrlsByPost(postId, FileType.POST_CONTENT));

        return postDetailResponse;
    }

    @Override
    public NormalPostPageResponse getNormalPostPageQuery(final GetPostsRequest dto) {
        // 인기 해시태그 정보
        final List<PopularHashtagResponse> popularHashtags =
            hashtagQueryRepository.findWeeklyPopularHashtagByCategory(dto.categoryId(), CategoryType.GENERAL);
        // 인기 게시글 정보
        final List<PopularPostResponse> popularPosts = findWeeklyPopularPostByCategory(dto.categoryId());
        // 기본 10개의 게시글 데이터
        final List<PostPreviewResponse> top10Posts = findAllPosts(null, dto);

        return new NormalPostPageResponse(popularHashtags, popularPosts, top10Posts);
    }

    @Override
    public List<PostPreviewResponse> searchPosts(final String keyword, final GetPostsRequest dto) {
        final List<PostPreviewResponse> postPreviewResponses = extractPostPreview(null, dto, keyword);
        final List<Long> postIds = postPreviewResponses.stream().map(PostPreviewResponse::getPostId).toList();
        final Map<Long, List<String>> hashtagsByPosts = hashtagQueryRepository.findAllHashtagByPosts(postIds);

        postPreviewResponses.forEach(postPreviewResponse -> {
            final Long postId = postPreviewResponse.getPostId();
            postPreviewResponse.setHashtags(hashtagsByPosts.getOrDefault(postId, Collections.emptyList()));
        });

        return postPreviewResponses;
    }

    private List<PopularPostResponse> findWeeklyPopularPostByCategory(final Long categoryId) {
        return queryFactory
            .select(Projections.fields(
                PopularPostResponse.class,
                post.id.as("postId"),
                post.title.title.as("title"),
                post.content.content.as("content"),
                post.thumbnail.as("thumbnailUrl")
            ))
            .from(post)
            .join(post.category, postCategory)
            .where(
                post.createdAt.after(LocalDateTime.now().minusDays(WEEKLY_BASE_DAY)),
                post.status.eq(PostStatus.ACTIVE),
                postCategory.id.eq(categoryId),
                postCategory.categoryType.eq(CategoryType.GENERAL)
            )
            .orderBy(post.viewCount.desc())
            .limit(5)
            .fetch();
    }


    private List<PostPreviewResponse> extractPostPreview(final Long memberId, final GetPostsRequest dto) {
        return queryFactory.select(PostQBeanGenerator.createPreview(PostPreviewResponse.class))
            .from(post)
            .join(member).on(member.id.eq(post.member.id))
            .where(
                PostQueryUtils.determineAuthorCheckingCondition(memberId),
                PostQueryUtils.deterMineLastContentCondition(
                    dto.lastPostId(),
                    dto.lastViewCount(),
                    dto.lastCommentCount(),
                    dto.postSortType()
                ),
                PostQueryUtils.determineCategoryCondition(dto.categoryId(), CategoryType.GENERAL),
                post.status.eq(PostStatus.ACTIVE)
            )
            .orderBy(PostQueryUtils.determinePostSortCondition(dto.postSortType()))
            .limit(PostQueryUtils.getLimitOrDefault(dto.limit()))
            .fetch();
    }

    private List<PostPreviewResponse> extractPostPreview(final Long memberId, final GetPostsRequest dto, final String keyword) {
        return queryFactory.select(PostQBeanGenerator.createPreview(PostPreviewResponse.class))
            .from(post)
            .join(member).on(member.id.eq(post.member.id))
            .where(
                PostQueryUtils.determineAuthorCheckingCondition(memberId),
                PostQueryUtils.deterMineLastContentCondition(
                    dto.lastPostId(),
                    dto.lastViewCount(),
                    dto.lastCommentCount(),
                    dto.postSortType()
                ),
                PostQueryUtils.determineCategoryCondition(dto.categoryId(), CategoryType.GENERAL),
                post.status.eq(PostStatus.ACTIVE),
                post.title.title.like("%" + keyword + "%")
            )
            .orderBy(PostQueryUtils.determinePostSortCondition(dto.postSortType()))
            .limit(PostQueryUtils.getLimitOrDefault(dto.limit()))
            .fetch();
    }

}
