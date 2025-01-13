package deepdivers.community.domain.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.IntegrationTest;
import deepdivers.community.domain.category.entity.PostCategory;
import deepdivers.community.domain.category.exception.CategoryExceptionCode;
import deepdivers.community.domain.common.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CategoryServiceTest extends IntegrationTest {

	@Autowired
	private CategoryService categoryService;

	@Test
	@DisplayName("ID로 카테고리 조회가 성공적으로 처리된다")
	void getCategoryByIdSuccessTest() {
		// given test.sql
		// When
		PostCategory foundCategory = categoryService.getCategoryById(1L);

		// Then
		assertThat(foundCategory.getId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("존재하지 않는 ID로 카테고리 조회 시 예외를 반환한다")
	void getCategoryByInvalidIdThrowsException() {
		// Given
		// When & Then
		assertThatThrownBy(() -> categoryService.getCategoryById(999L))
			.isInstanceOf(NotFoundException.class)
			.hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionCode.CATEGORY_NOT_FOUND);
	}

	@Test
	void 범용_카테고리가_아닐_경우_예외가_발생한다() {
		// Given
		PostCategory category = categoryService.getCategoryById(2L);

		// When & Then
		assertThatThrownBy(() -> categoryService.validateGeneralCategory(category))
			.isInstanceOf(NotFoundException.class)
			.hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionCode.INVALID_GENERAL_CATEGORY);
	}
	@Test
	void 프로젝트_카테고리가_아닐_경우_예외가_발생한다() {
		// Given
		PostCategory category = categoryService.getCategoryById(1L);

		// When & Then
		assertThatThrownBy(() -> categoryService.validateGeneralCategory(category))
			.isInstanceOf(NotFoundException.class)
			.hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionCode.INVALID_PROJECT_CATEGORY);
	}

}
