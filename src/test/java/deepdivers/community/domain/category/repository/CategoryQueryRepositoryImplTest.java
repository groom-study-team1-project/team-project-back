package deepdivers.community.domain.category.repository;

import static org.assertj.core.api.Assertions.assertThat;

import deepdivers.community.domain.RepositoryTest;
import deepdivers.community.domain.category.dto.response.CategoryResponse;
import deepdivers.community.domain.category.dto.response.MemberPostCountByCategoryResponse;
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

    @Test
    void 모든_카테고리_별_작성한_게시글_수를_가져온다() {
        // given test.sql
        // when
        List<MemberPostCountByCategoryResponse> result = categoryQueryRepository.countMemberPostsByCategory(1L);

        // then
        assertThat(result.size()).isEqualTo(4);
    }

    @Test
    void 사용자가_카테고리별로_작성한_게시글_수를_가져온다() {
        // given test.sql
        // when
        List<MemberPostCountByCategoryResponse> result = categoryQueryRepository.countMemberPostsByCategory(1L);

        // then
        assertThat(result.getFirst().getPostCount()).isEqualTo(1);
        assertThat(result.get(1).getPostCount()).isEqualTo(0);
        assertThat(result.get(2).getPostCount()).isEqualTo(0);
        assertThat(result.get(3).getPostCount()).isEqualTo(0);
    }

}