package deepdivers.community.domain.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberPostCountByCategoryResponse {

    private Long categoryId;
    private String categoryTitle;
    private Long postCount;

}