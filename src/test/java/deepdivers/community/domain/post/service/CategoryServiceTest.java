package deepdivers.community.domain.post.service;

import static org.assertj.core.api.Assertions.*;

import deepdivers.community.global.config.LocalStackTestConfig;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import deepdivers.community.domain.post.dto.response.CategoryResponse;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.vo.CategoryStatus;
import deepdivers.community.domain.post.repository.CategoryRepository;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
@Transactional
@Import(LocalStackTestConfig.class)
class CategoryServiceTest {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CategoryRepository categoryRepository;

	private PostCategory category1;
	private PostCategory category2;

	@BeforeEach
	void setUp() {
		categoryRepository.deleteAll(); // 테스트 전에 모든 카테고리 삭제

		// 카테고리 생성
		category1 = PostCategory.createCategory("첫 번째 카테고리", "첫 번째 카테고리 설명", CategoryStatus.ACTIVE);
		category2 = PostCategory.createCategory("두 번째 카테고리", "두 번째 카테고리 설명", CategoryStatus.ACTIVE);

		categoryRepository.save(category1);
		categoryRepository.save(category2);
	}

	@Test
	@DisplayName("전체 카테고리 조회 성공 통합 테스트")
	void getAllCategoriesSuccessIntegrationTest() {
		// When
		List<CategoryResponse> response = categoryService.getAllCategories();

		// Then
		assertThat(response).hasSize(2);  // 카테고리 2개가 존재하는지 확인
		assertThat(response.get(0).title()).isEqualTo("첫 번째 카테고리");
		assertThat(response.get(0).description()).isEqualTo("첫 번째 카테고리 설명");
		assertThat(response.get(1).title()).isEqualTo("두 번째 카테고리");
		assertThat(response.get(1).description()).isEqualTo("두 번째 카테고리 설명");
	}

	@Test
	@DisplayName("전체 카테고리가 없을 때 빈 리스트 반환 통합 테스트")
	void getAllCategoriesReturnsEmptyListWhenNoCategoriesExist() {
		// Given
		categoryRepository.deleteAll(); // 모든 카테고리 삭제

		// When
		List<CategoryResponse> response = categoryService.getAllCategories();

		// Then
		assertThat(response).isEmpty();  // 카테고리가 없을 때 빈 리스트 반환
	}
}
