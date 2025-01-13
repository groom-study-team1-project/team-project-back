package deepdivers.community.domain.category.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostCategoryTest {

	@Test
	@DisplayName("General Category를 성공적으로 생성하는 것을 확인한다.")
	void postCategoryShouldBeCreatedSuccessfully() {
		// given
		String title = "Category Title";
		String description = "Category description";

		// when
		PostCategory postCategory = new PostCategory(title, description, CategoryType.GENERAL);

		// then
		assertThat(postCategory).isNotNull();
		assertThat(postCategory.getTitle()).isEqualTo(title);
		assertThat(postCategory.getDescription()).isEqualTo(description);
		assertThat(postCategory.getCategoryType()).isEqualTo(CategoryType.GENERAL);
	}

	@Test
	void Project_Category를_성공적으로_생성하는_것을_확인한다() {
		// given
		String title = "Category Title";
		String description = "Category description";

		// when
		PostCategory postCategory = new PostCategory(title, description, CategoryType.PROJECT);

		// then
		assertThat(postCategory).isNotNull();
		assertThat(postCategory.getTitle()).isEqualTo(title);
		assertThat(postCategory.getDescription()).isEqualTo(description);
		assertThat(postCategory.getCategoryType()).isEqualTo(CategoryType.PROJECT);
	}

}
