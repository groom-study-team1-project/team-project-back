package deepdivers.community.domain.category.repository;

import static deepdivers.community.domain.category.entity.QPostCategory.postCategory;
import static deepdivers.community.domain.post.entity.QPost.post;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.category.controller.interfaces.CategoryQueryRepository;
import deepdivers.community.domain.category.dto.response.CategoryResponse;
import deepdivers.community.domain.category.dto.response.MemberPostCountByCategoryResponse;
import deepdivers.community.domain.post.entity.PostStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return jpaQueryFactory
            .select(Projections.fields(
                CategoryResponse.class,
                postCategory.id.as("id"),
                postCategory.title.as("title"),
                postCategory.categoryType.as("categoryType"),
                postCategory.description.as("description")
                )
            )
        .from(postCategory)
        .orderBy(postCategory.id.asc())
        .fetch();
    }

    @Override
    public List<MemberPostCountByCategoryResponse> countMemberPostsByCategory(final Long memberId) {
        return jpaQueryFactory
            .select(Projections.fields(
                MemberPostCountByCategoryResponse.class,
                postCategory.id.as("categoryId"),
                postCategory.title.as("categoryTitle"),
                postCategory.categoryType.as("categoryType"),
                post.count().coalesce(0L).as("postCount")
            ))
            .from(postCategory)
            .leftJoin(post)
            .on(
                postCategory.id.eq(post.category.id)
                    .and(post.member.id.eq(memberId))
                    .and(post.status.eq(PostStatus.ACTIVE))
            )
            .groupBy(postCategory.id)
            .orderBy(postCategory.id.asc())
            .fetch();
    }

}
