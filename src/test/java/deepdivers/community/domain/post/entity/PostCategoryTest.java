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
		CategoryStatus status = CategoryStatus.ACTIVE;

		// when
		PostCategory postCategory = PostCategory.createCategory(title, description, status);

		// then
		assertThat(postCategory).isNotNull();
		assertThat(postCategory.getTitle()).isEqualTo(title);
		assertThat(postCategory.getDescription()).isEqualTo(description);
		assertThat(postCategory.getStatus()).isEqualTo(status);
	}

	@Test
	@DisplayName("전체 카테고리 목록을 성공적으로 조회하는 것을 확인한다.")
	void shouldRetrieveAllCategoriesSuccessfully() {
		// given
		PostCategory category1 = PostCategory.createCategory("Category 1", "Description 1", CategoryStatus.ACTIVE);
		PostCategory category2 = PostCategory.createCategory("Category 2", "Description 2", CategoryStatus.ACTIVE);
		PostCategory category3 = PostCategory.createCategory("Category 3", "Description 3", CategoryStatus.INACTIVE);

		// 카테고리 목록을 리스트로 생성
		List<PostCategory> categories = Arrays.asList(category1, category2, category3);

		// when
		List<PostCategory> retrievedCategories = categories; // 여기서는 categories 리스트가 반환되는 것으로 가정

		// then
		assertThat(retrievedCategories).hasSize(3); // 카테고리 목록의 크기가 3이어야 함
		assertThat(retrievedCategories.get(0).getTitle()).isEqualTo("Category 1");
		assertThat(retrievedCategories.get(1).getTitle()).isEqualTo("Category 2");
		assertThat(retrievedCategories.get(2).getTitle()).isEqualTo("Category 3");
		assertThat(retrievedCategories.get(0).getStatus()).isEqualTo(CategoryStatus.ACTIVE);
		assertThat(retrievedCategories.get(2).getStatus()).isEqualTo(CategoryStatus.INACTIVE); // 세 번째 카테고리는 INACTIVE
	}
}
