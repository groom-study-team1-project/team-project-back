package deepdivers.community.domain.category.repository;

import static deepdivers.community.domain.category.entity.QPostCategory.postCategory;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.category.controller.interfaces.CategoryQueryRepository;
import deepdivers.community.domain.category.dto.response.CategoryResponse;
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
                postCategory.description.as("description")
                )
            )
        .from(postCategory)
        .fetch();
    }

}
