package deepdivers.community.domain.post.controller.open;

import deepdivers.community.domain.post.controller.docs.CategoryOpenControllerDocs;
import deepdivers.community.domain.post.dto.response.CategoryResponse;
import deepdivers.community.domain.post.service.CategoryService;
import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.dto.response.statustype.CategoryStatusType; // 상태 타입 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open/categories")
public class CategoryOpenController implements CategoryOpenControllerDocs {

	private final CategoryService categoryService;

	@Override
	@GetMapping
	public ResponseEntity<API<List<CategoryResponse>>> getAllCategories() {
		List<CategoryResponse> categories = categoryService.getAllCategories();
		// 상태 코드를 함께 전달해야 하므로 PostStatusType.POST_VIEW_SUCCESS를 전달합니다.
		return ResponseEntity.ok(API.of(CategoryStatusType.CATEGORY_VIEW_SUCCESS, categories));
	}
}
