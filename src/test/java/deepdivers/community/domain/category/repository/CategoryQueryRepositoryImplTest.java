package deepdivers.community.domain.category.repository;

import static org.assertj.core.api.Assertions.assertThat;

import deepdivers.community.domain.RepositoryTest;
import deepdivers.community.domain.category.dto.response.CategoryResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

class CategoryQueryRepositoryImplTest extends RepositoryTest {

    @Test
    void 모든_카테고리_정보를_가져온다() {
        // given test.sql

        // when
        List<CategoryResponse> allCategories = categoryQueryRepository.getAllCategories();

        // then
        assertThat(allCategories.size()).isEqualTo(4);
    }

}