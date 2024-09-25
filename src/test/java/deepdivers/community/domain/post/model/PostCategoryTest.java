package deepdivers.community.domain.post.model;

import static org.assertj.core.api.Assertions.assertThat;

import deepdivers.community.domain.post.model.vo.CategoryStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostCategoryTest {

	@Test
	@DisplayName("PostCategory 객체를 성공적으로 생성하는 것을 확인한다.")
	void postCategoryShouldBeCreatedSuccessfully() {
		// given
		String title = "Category Title";
		String description = "Category description";
		CategoryStatus status = CategoryStatus.ACTIVE;

		// when
		PostCategory postCategory = PostCategory.createCategory(title, description, status);

		// then
		assertThat(postCategory).isNotNull();
		assertThat(postCategory.getTitle()).isEqualTo(title);
		assertThat(postCategory.getDescription()).isEqualTo(description);
		assertThat(postCategory.getStatus()).isEqualTo(status);
	}
}
