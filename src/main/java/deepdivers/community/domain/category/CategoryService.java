package deepdivers.community.domain.category;

import deepdivers.community.domain.category.dto.CategoryResponse;
import deepdivers.community.domain.category.entity.PostCategory;
import deepdivers.community.domain.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public List<CategoryResponse> getAllCategories() {
		List<PostCategory> categories = categoryRepository.findAll();
		return categories.stream()
			.map(category -> new CategoryResponse(
				category.getId(),
				category.getTitle(),
				category.getDescription()
			))
			.collect(Collectors.toList());
	}

	public PostCategory getCategoryById(Long categoryId) {
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new BadRequestException(CategoryExceptionCode.CATEGORY_NOT_FOUND));
	}

}
