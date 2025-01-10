package deepdivers.community.domain.category.controller.interfaces;

import deepdivers.community.domain.category.dto.response.CategoryResponse;
import deepdivers.community.domain.category.dto.response.MemberPostCountByCategoryResponse;
import java.util.List;

public interface CategoryQueryRepository {

    List<CategoryResponse> getAllCategories();

    List<MemberPostCountByCategoryResponse> countMemberPostsByCategory(Long memberId);

}
