package deepdivers.community.domain.category.controller.interfaces;

import deepdivers.community.domain.category.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryQueryRepository {

    List<CategoryResponse> getAllCategories();

}
