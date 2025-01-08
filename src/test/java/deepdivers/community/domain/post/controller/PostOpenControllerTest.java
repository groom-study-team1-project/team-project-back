package deepdivers.community.domain.post.controller;

import static deepdivers.community.domain.post.dto.response.statustype.PostStatusType.POST_VIEW_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.aspect.ViewCountAspect;
import deepdivers.community.domain.post.controller.open.PostOpenController;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.repository.PostQueryRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.utils.PostDtoGenerator;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@WebMvcTest(controllers = PostOpenController.class)
@Import(ViewCountAspect.class)
class PostOpenControllerTest extends ControllerTest {

	@MockBean private PostQueryRepository postQueryRepository;
    @MockBean private PostRepository postRepository;
	@Autowired
	MockMvc mockMvc;

	@Test
	@DisplayName("게시글 상세 조회 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void getPostByIdSuccessfullyReturns200OK() {
		// given
		PostDetailResponse responseBody = new PostDetailResponse(List.of("image"), false, false);
		given(postQueryRepository.readPostByPostId(anyLong(), anyLong())).willReturn(responseBody);

		// when
		API<PostDetailResponse> response = RestAssuredMockMvc.given().log().all()
			.pathParam("postId", 1L)
			.when().get("/open/posts/{postId}")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		API<PostDetailResponse> mockResponse = API.of(POST_VIEW_SUCCESS, responseBody);
		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

	@Test
	void 전체_게시글을_조회할_수_있다() {
		// given
		List<PostPreviewResponse> mockQueryResult = List.of(PostDtoGenerator.generatePostPreview());
		given(postQueryRepository.findAllPosts(isNull(), anyLong(), anyLong())).willReturn(mockQueryResult);

		// when
		API<List<PostPreviewResponse>> response = RestAssuredMockMvc.given().log().all()
				.queryParam("categoryId", 1L)
				.queryParam("lastPostId", 1L)
				.when().get("/open/posts")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		API<List<PostPreviewResponse>> mockResponse = API.of(POST_VIEW_SUCCESS, mockQueryResult);
		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

	@Test
	void 조회_조건이_없어도_조회를_할_수_있다() {
		// given
		List<PostPreviewResponse> mockQueryResult = List.of(PostDtoGenerator.generatePostPreview());
		given(postQueryRepository.findAllPosts(isNull(), isNull(), isNull())).willReturn(mockQueryResult);

		// when
		API<List<PostPreviewResponse>> response = RestAssuredMockMvc.given().log().all()
				.when().get("/open/posts")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		API<List<PostPreviewResponse>> mockResponse = API.of(POST_VIEW_SUCCESS, mockQueryResult);
		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

	@Test
	void 조회_조건이_카테고리_ID만_존재할_경우_조회할_수_있다() {
		// given
		List<PostPreviewResponse> mockQueryResult = List.of(PostDtoGenerator.generatePostPreview());
		given(postQueryRepository.findAllPosts(isNull(), isNull(), anyLong())).willReturn(mockQueryResult);

		// when
		API<List<PostPreviewResponse>> response = RestAssuredMockMvc.given().log().all()
				.queryParam("categoryId", 1L)
				.when().get("/open/posts")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		API<List<PostPreviewResponse>> mockResponse = API.of(POST_VIEW_SUCCESS, mockQueryResult);
		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

	@Test
	void 조회_조건이_마지막_게시글_정보만_있을_경우에도_조회할_수_있다() {
		// given
		List<PostPreviewResponse> mockQueryResult = List.of(PostDtoGenerator.generatePostPreview());
		given(postQueryRepository.findAllPosts(isNull(), anyLong(), isNull())).willReturn(mockQueryResult);

		// when
		API<List<PostPreviewResponse>> response = RestAssuredMockMvc.given().log().all()
			.queryParam("lastPostId", 1L)
			.when().get("/open/posts")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		API<List<PostPreviewResponse>> mockResponse = API.of(POST_VIEW_SUCCESS, mockQueryResult);
		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

	@Test
	void 내가_쓴_게시글_목록에_대해_조회할_수_있다() {
		// given
		List<PostPreviewResponse> mockQueryResult = List.of(PostDtoGenerator.generatePostPreview());
		given(postQueryRepository.findAllPosts(anyLong(), isNull(), isNull())).willReturn(mockQueryResult);

		// when
		API<List<PostPreviewResponse>> response = RestAssuredMockMvc.given().log().all()
			.pathParam("memberId", 1L)
			.when().get("/open/posts/me/{memberId}")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		API<List<PostPreviewResponse>> mockResponse = API.of(PostStatusType.MY_POSTS_GETTING_SUCCESS, mockQueryResult);
		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

}

