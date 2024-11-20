package deepdivers.community.domain.post.service;

import deepdivers.community.domain.post.dto.response.CategoryResponse;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.global.exception.model.BadRequestException;
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
				.orElseThrow(() -> new BadRequestException(CategoryExceptionType.CATEGORY_NOT_FOUND));
	}

}
