package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.controller.open.CategoryOpenController;
import deepdivers.community.domain.post.dto.response.CategoryResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.service.CategoryService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = CategoryOpenController.class)
class CategoryOpenControllerTest extends ControllerTest {

	@MockBean
	private CategoryService categoryService; // CategoryService를 MockBean으로 선언

	private CategoryResponse mockCategoryResponse;

	@BeforeEach
	void init() {
		// 카테고리 조회 시 사용할 mock 데이터 생성
		mockCategoryResponse = new CategoryResponse(
			1L,              // categoryId
			"카테고리 제목",   // title
			"카테고리 설명"    // description
		);
	}

	// 비회원 카테고리 목록 조회 성공 테스트
	@Test
	@DisplayName("비회원이 카테고리 목록 조회에 성공하면 200 OK와 카테고리 목록을 반환한다")
	void getAllCategoriesSuccessfullyReturns200OKForOpen() {
		// given
		List<CategoryResponse> mockCategoryResponses = Arrays.asList(mockCategoryResponse, mockCategoryResponse); // 여러 카테고리 생성
		API<List<CategoryResponse>> mockResponse = API.of(PostStatusType.POST_VIEW_SUCCESS, mockCategoryResponses);

		given(categoryService.getAllCategories()).willReturn(mockCategoryResponses);

		// when
		API<List<CategoryResponse>> response = RestAssuredMockMvc
			.given().log().all()
			.header("X-Forwarded-For", "127.0.0.1")  // IP 주소
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/open/categories")
			.then().log().all()
			.status(HttpStatus.OK) // 200 OK 반환 기대
			.extract()
			.as(new TypeRef<API<List<CategoryResponse>>>() {});  // API<List<CategoryResponse>>로 변환

		// then
		assertThat(response.getResult()).hasSize(2);  // 2개의 카테고리가 반환됨을 확인
		assertThat(response.getResult().get(0).title()).isEqualTo("카테고리 제목");
		assertThat(response.getResult().get(0).description()).isEqualTo("카테고리 설명");
	}
}
