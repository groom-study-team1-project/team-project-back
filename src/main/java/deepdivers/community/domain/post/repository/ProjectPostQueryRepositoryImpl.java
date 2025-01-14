package deepdivers.community.domain.post.repository;

import static com.querydsl.core.types.ExpressionUtils.and;
import static deepdivers.community.domain.member.entity.QMember.member;
import static deepdivers.community.domain.post.entity.QPost.post;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.category.entity.CategoryType;
import deepdivers.community.domain.file.application.interfaces.FileQueryRepository;
import deepdivers.community.domain.hashtag.controller.interfaces.HashtagQueryRepository;
import deepdivers.community.domain.post.controller.interfaces.ProjectPostQueryRepository;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPreviewResponse;
import deepdivers.community.domain.post.entity.PostStatus;
import deepdivers.community.domain.post.repository.utils.PostQBeanGenerator;
import deepdivers.community.domain.post.repository.utils.PostQueryUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectPostQueryRepositoryImpl implements ProjectPostQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final HashtagQueryRepository hashtagQueryRepository;
    private final FileQueryRepository fileQueryRepository;

    @Override
    public List<ProjectPostPreviewResponse> findAllPosts(Long memberId, GetPostsRequest dto) {
        final List<ProjectPostPreviewResponse> postPreviewResponses = extractPostPreview(memberId, dto);
        final List<Long> postIds = postPreviewResponses.stream().map(PostPreviewResponse::getPostId).toList();
        final Map<Long, List<String>> hashtagsByPosts = hashtagQueryRepository.findAllHashtagByPosts(postIds);
        final Map<Long, List<String>> slideImageUrlByPosts = fileQueryRepository.findAllSlideImageUrlByPosts(postIds);

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

    private List<ProjectPostPreviewResponse> extractPostPreview(final Long memberId, final GetPostsRequest dto) {
        return queryFactory.select(PostQBeanGenerator.createPreview(ProjectPostPreviewResponse.class, post, member))
            .from(post)
            .join(member).on(member.id.eq(post.member.id))
            .where(
                PostQueryUtils.determineAuthorCheckingCondition(memberId),
                PostQueryUtils.deterMineLastContentCondition(dto.lastPostId()),
                PostQueryUtils.determineCategoryCondition(dto.categoryId(), CategoryType.PROJECT),
                post.status.eq(PostStatus.ACTIVE)
            )
            .orderBy(PostQueryUtils.determinePostSortCondition(dto.postSortType()))
            .limit(PostQueryUtils.getLimitOrDefault(dto.limit()))
            .fetch();
    }


}
