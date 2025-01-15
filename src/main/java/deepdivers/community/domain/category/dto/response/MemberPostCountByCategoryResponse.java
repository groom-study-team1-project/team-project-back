package deepdivers.community.domain.category.dto.response;

import deepdivers.community.domain.category.entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberPostCountByCategoryResponse {

    private Long categoryId;
    private String categoryTitle;
    private CategoryType categoryType;
    private Long postCount;

}