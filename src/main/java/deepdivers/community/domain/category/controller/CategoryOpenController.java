package deepdivers.community.domain.category.controller;

import deepdivers.community.domain.category.controller.docs.CategoryOpenControllerDocs;
import deepdivers.community.domain.category.controller.interfaces.CategoryQueryRepository;
import deepdivers.community.domain.category.dto.code.CategoryStatusCode;
import deepdivers.community.domain.category.dto.response.CategoryResponse;
import deepdivers.community.domain.common.dto.response.API;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open/categories")
public class CategoryOpenController implements CategoryOpenControllerDocs {

	private final CategoryQueryRepository categoryQueryRepository;

	@Override
	@GetMapping
	public ResponseEntity<API<List<CategoryResponse>>> getAllCategories() {
		List<CategoryResponse> categories = categoryQueryRepository.getAllCategories();
		return ResponseEntity.ok(API.of(CategoryStatusCode.CATEGORY_VIEW_SUCCESS, categories));
	}
}
