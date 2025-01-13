package deepdivers.community.domain.category.service;

import deepdivers.community.domain.category.dto.response.CategoryResponse;
import deepdivers.community.domain.category.entity.PostCategory;
import deepdivers.community.domain.category.exception.CategoryExceptionCode;
import deepdivers.community.domain.category.repository.jpa.CategoryRepository;
import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public PostCategory getCategoryById(Long categoryId) {
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new NotFoundException(CategoryExceptionCode.CATEGORY_NOT_FOUND));
	}

	public void validateGeneralCategory(final PostCategory category) {
		if (!category.isGeneralCategory()) {
			throw new BadRequestException(CategoryExceptionCode.INVALID_GENERAL_CATEGORY);
		}
	}

	public void validateProjectCategory(final PostCategory category) {
		if (!category.isProjectCategory()) {
			throw new BadRequestException(CategoryExceptionCode.INVALID_PROJECT_CATEGORY);
		}
	}

}
