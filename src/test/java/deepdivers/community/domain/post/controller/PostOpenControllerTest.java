package deepdivers.community.domain.post.controller;

import static deepdivers.community.domain.post.dto.code.PostStatusCode.POST_VIEW_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.post.aspect.ViewCountAspect;
import deepdivers.community.domain.post.controller.interfaces.ProjectPostQueryRepository;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.dto.code.PostStatusCode;
import deepdivers.community.domain.post.controller.interfaces.PostQueryRepository;
import deepdivers.community.domain.post.dto.response.ProjectPostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPreviewResponse;
import deepdivers.community.domain.post.repository.jpa.PostRepository;
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
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PostOpenController.class)
@Import(ViewCountAspect.class)
class PostOpenControllerTest extends ControllerTest {

	@MockBean private PostQueryRepository postQueryRepository;
	@MockBean private ProjectPostQueryRepository projectPostQueryRepository;
    @MockBean private PostRepository postRepository;

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
		given(postQueryRepository.findAllPosts(isNull(), any(GetPostsRequest.class))).willReturn(mockQueryResult);

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
		given(postQueryRepository.findAllPosts(isNull(), any(GetPostsRequest.class))).willReturn(mockQueryResult);

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
		given(postQueryRepository.findAllPosts(isNull(), any(GetPostsRequest.class))).willReturn(mockQueryResult);

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
		given(postQueryRepository.findAllPosts(isNull(), any(GetPostsRequest.class))).willReturn(mockQueryResult);

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
		given(postQueryRepository.findAllPosts(anyLong(), any(GetPostsRequest.class))).willReturn(mockQueryResult);

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
		API<List<PostPreviewResponse>> mockResponse = API.of(PostStatusCode.MY_POSTS_GETTING_SUCCESS, mockQueryResult);
		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

	@Test
	void 프로젝트_게시글_전체_조회가_성공한다() {
		// given
		List<ProjectPostPreviewResponse> mockResp = List.of(PostDtoGenerator.generateProjectPostPreview());
		given(projectPostQueryRepository.findAllPosts(isNull(), any(GetPostsRequest.class))).willReturn(mockResp);

		// when
		API<List<ProjectPostPreviewResponse>> response = RestAssuredMockMvc.given().log().all()
			.when().get("/open/posts/project")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		API<List<ProjectPostPreviewResponse>> expected = API.of(PostStatusCode.POST_VIEW_SUCCESS, mockResp);
		assertThat(response).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void 사용자가_작성한_프로젝트_게시글_조회가_성공한다() {
		// given
		List<ProjectPostPreviewResponse> mockResp = List.of(PostDtoGenerator.generateProjectPostPreview());
		given(projectPostQueryRepository.findAllPosts(anyLong(), any(GetPostsRequest.class))).willReturn(mockResp);

		// when
		API<List<ProjectPostPreviewResponse>> response = RestAssuredMockMvc.given().log().all()
			.pathParam("memberId", 1L).when().get("/open/posts/project/me/{memberId}")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		API<List<ProjectPostPreviewResponse>> expected = API.of(PostStatusCode.POST_VIEW_SUCCESS, mockResp);
		assertThat(response).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void 프로젝트_게시글_상세_조회가_성공한다() {
		// given
		ProjectPostDetailResponse mockResp = PostDtoGenerator.generateProjectDetail();
		given(projectPostQueryRepository.readPostByPostId(anyLong(), anyLong())).willReturn(mockResp);

		// when
		API<ProjectPostDetailResponse> response = RestAssuredMockMvc.given().log().all()
			.pathParam("postId", 1L).when().get("/open/posts/project/{postId}")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		API<ProjectPostDetailResponse> expected = API.of(PostStatusCode.POST_VIEW_SUCCESS, mockResp);
		assertThat(response).usingRecursiveComparison().isEqualTo(expected);
	}

}

