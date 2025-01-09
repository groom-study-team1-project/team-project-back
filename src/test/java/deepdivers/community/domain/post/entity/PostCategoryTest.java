package deepdivers.community.domain.post.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class PostCategoryTest {

	@Test
	@DisplayName("PostCategory 객체를 성공적으로 생성하는 것을 확인한다.")
	void postCategoryShouldBeCreatedSuccessfully() {
		// given
		String title = "Category Title";
		String description = "Category description";

		// when
		PostCategory postCategory = PostCategory.createCategory(title, description);

		// then
		assertThat(postCategory).isNotNull();
		assertThat(postCategory.getTitle()).isEqualTo(title);
		assertThat(postCategory.getDescription()).isEqualTo(description);
	}

}
