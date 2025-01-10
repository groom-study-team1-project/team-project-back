package deepdivers.community.domain.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.IntegrationTest;
import deepdivers.community.domain.category.exception.CategoryExceptionCode;
import deepdivers.community.domain.category.repository.jpa.CategoryRepository;
import deepdivers.community.domain.category.dto.response.CategoryResponse;
import deepdivers.community.domain.category.entity.PostCategory;
import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.common.exception.NotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CategoryServiceTest extends IntegrationTest {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CategoryRepository categoryRepository;

	private PostCategory category1;
	private PostCategory category2;

	@BeforeEach
	void setUp() {
		category1 = PostCategory.createCategory("Category 1", "Description 1");
		category2 = PostCategory.createCategory("Category 2", "Description 2");

		categoryRepository.save(category1);
		categoryRepository.save(category2);
	}

	@Test
	@DisplayName("모든 카테고리 조회가 성공적으로 처리된다")
	void getAllCategoriesSuccessTest() {
		// When
		List<CategoryResponse> responses = categoryService.getAllCategories();

		// Then
		assertThat(responses).isNotNull();
		assertThat(responses).hasSize(6);

		CategoryResponse response1 = responses.get(4);
		assertThat(response1.getTitle()).isEqualTo(category1.getTitle());
		assertThat(response1.getDescription()).isEqualTo(category1.getDescription());

		CategoryResponse response2 = responses.get(5);
		assertThat(response2.getTitle()).isEqualTo(category2.getTitle());
		assertThat(response2.getDescription()).isEqualTo(category2.getDescription());
	}

	@Test
	@DisplayName("ID로 카테고리 조회가 성공적으로 처리된다")
	void getCategoryByIdSuccessTest() {
		// When
		PostCategory foundCategory = categoryService.getCategoryById(category1.getId());

		// Then
		assertThat(foundCategory).isNotNull();
		assertThat(foundCategory.getId()).isEqualTo(category1.getId());
		assertThat(foundCategory.getTitle()).isEqualTo(category1.getTitle());
		assertThat(foundCategory.getDescription()).isEqualTo(category1.getDescription());
	}

	@Test
	@DisplayName("존재하지 않는 ID로 카테고리 조회 시 예외를 반환한다")
	void getCategoryByInvalidIdThrowsException() {
		// Given
		Long invalidCategoryId = 999L;

		// When & Then
		assertThatThrownBy(() -> categoryService.getCategoryById(invalidCategoryId))
				.isInstanceOf(NotFoundException.class)
				.hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionCode.CATEGORY_NOT_FOUND);
	}

}
