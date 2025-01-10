package deepdivers.community.domain.category.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.category.controller.interfaces.CategoryQueryRepository;
import deepdivers.community.domain.category.dto.code.CategoryStatusCode;
import deepdivers.community.domain.category.dto.response.CategoryResponse;
import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.post.dto.code.PostStatusCode;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = CategoryOpenController.class)
class CategoryOpenControllerTest extends ControllerTest {

	@MockBean private CategoryQueryRepository categoryQueryRepository;

	// 비회원 카테고리 목록 조회 성공 테스트
	@Test
	@DisplayName("카테고리 목록 조회에 성공하면 200 OK와 카테고리 목록을 반환한다")
	void getAllCategoriesSuccessfullyReturns200OKForOpen() {
		// given
		List<CategoryResponse> mockCategoryResponses = List.of(new CategoryResponse(1L, "title", "desc"));
		given(categoryQueryRepository.getAllCategories()).willReturn(mockCategoryResponses);

		// when
		API<List<CategoryResponse>> response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/open/categories")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {});

		// then
		API<List<CategoryResponse>> mockResponse = API.of(CategoryStatusCode.CATEGORY_VIEW_SUCCESS, mockCategoryResponses);
		assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
	}
}
