package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.PostRequestFactory;
import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.post.dto.code.PostStatusCode;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import deepdivers.community.domain.like.service.LikeService;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.post.service.ProjectPostService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = PostApiController.class)
class PostApiControllerTest extends ControllerTest {

	@MockBean
	private PostService postService;
	@MockBean
	private ProjectPostService projectPostService;

	@BeforeEach
	void init() {
		mockingAuthArgumentResolver();
	}

	@Test
	@DisplayName("게시글 생성 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void createPostSuccessfullyReturns200OK() {
		// given
		PostSaveRequest request = new PostSaveRequest(
				"Post Title",
				"Post Content",
				"",
				1L,
				List.of("tag1", "tag2"),
				List.of("http/temp/f.jpeg")
		);
		PostSaveResponse responseBody = new PostSaveResponse(1L);
		API<PostSaveResponse> mockResponse = API.of(PostStatusCode.POST_CREATE_SUCCESS, responseBody);

		given(postService.createPost(any(PostSaveRequest.class), any(Member.class))).willReturn(mockResponse);

		// when
		API<PostSaveResponse> response = RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/upload")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		assertThat(response).isNotNull();
		assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("게시글 생성 요청에서 제목이 없으면 400 BadRequest를 반환한다")
	void createPostWithoutTitleReturns400BadRequest() {
		// given
		PostSaveRequest request = new PostSaveRequest(
				null,
				"Post Content",
				"",
				1L,
				List.of("tag1", "tag2"),
				List.of("http/temp/f.jpeg")
		);

		// when, then
		RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/upload")
				.then().log().all()
				.status(HttpStatus.BAD_REQUEST)
				.body("message", containsString("게시글 제목은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 생성 요청에서 내용이 없으면 400 BadRequest를 반환한다")
	void createPostWithoutContentReturns400BadRequest() {
		// given
		PostSaveRequest request = new PostSaveRequest(
				"Post Title",
				null,
				"",
				1L,
				List.of("tag1", "tag2"),
				List.of("http/temp/f.jpeg")
		);

		// when, then
		RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/upload")
				.then().log().all()
				.status(HttpStatus.BAD_REQUEST)
				.body("message", containsString("게시글 내용은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 생성 요청에서 카테고리 ID가 없으면 400 BadRequest를 반환한다")
	void createPostWithoutCategoryIdReturns400BadRequest() {
		// given
		PostSaveRequest request = new PostSaveRequest(
				"Post Title",
				"Post Content",
				"",
				null,
				List.of("tag1", "tag2"),
				List.of("http/temp/f.jpeg")
		);

		// when, then
		RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/upload")
				.then().log().all()
				.status(HttpStatus.BAD_REQUEST)
				.body("message", containsString("카테고리 선택은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 작성 시 썸네일 없이 작성해도 200 OK를 반환한다")
	void createPostWithoutThumbnailReturns200OK() {
		// given
		PostSaveRequest request = new PostSaveRequest(
				"Post Title",
				"Post Content",
				null,
				1L,
				List.of("tag1", "tag2"),
				List.of("http/temp/f.jpeg")
		);

		PostSaveResponse responseBody = new PostSaveResponse(1L);
		API<PostSaveResponse> mockResponse = API.of(PostStatusCode.POST_CREATE_SUCCESS, responseBody);

		given(postService.createPost(any(PostSaveRequest.class), any(Member.class))).willReturn(mockResponse);

		// when
		API<PostSaveResponse> response = RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/upload")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		assertThat(response).isNotNull();
		assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("게시글 작성 시 해시태그 없이 작성해도 200 OK를 반환한다")
	void createPostWithoutHashtagsReturns200OK() {
		// given
		PostSaveRequest request = new PostSaveRequest("", "", "", 1L, null, List.of());

		// when
		RestAssuredMockMvc.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/upload")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST)
			.body("code", equalTo(101));
	}

	@Test
	@DisplayName("게시글 작성 시 이미지가 null일 경우 예외가 발생한다.")
	void createPostWithoutImagesReturns200OK() {
		// given
		PostSaveRequest request = new PostSaveRequest("", "", "", 1L, List.of(), null);

		// when
		RestAssuredMockMvc.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/upload")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST)
			.body("code", equalTo(101));
	}

	@Test
	@DisplayName("게시글 수정 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void updateSuccessfullyReturns200OK() {
		// given
		Long postId = 1L;
		PostSaveRequest request = PostRequestFactory.createPostSaveRequest();
		PostSaveResponse responseBody = new PostSaveResponse(postId);
		API<PostSaveResponse> mockResponse = API.of(PostStatusCode.POST_UPDATE_SUCCESS, responseBody);

		given(postService.updatePost(eq(postId), any(PostSaveRequest.class), any(Member.class))).willReturn(mockResponse);

		// when
		API<PostSaveResponse> response = RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/edit/{postId}", String.valueOf(postId))
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		assertThat(response).isNotNull();
		assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("게시글 수정 요청에서 제목이 없으면 400 BadRequest를 반환한다")
	void updateWithoutTitleReturns400BadRequest() {
		// given
		Long postId = 1L;
		PostSaveRequest request = new PostSaveRequest(null, "", "", 1L, List.of(), null);

		// when, then
		RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/edit/{postId}", String.valueOf(postId))
				.then().log().all()
				.status(HttpStatus.BAD_REQUEST)
				.body("message", containsString("게시글 제목은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 수정 요청에서 카테고리 ID가 없으면 400 BadRequest를 반환한다")
	void updateWithoutCategoryIdReturns400BadRequest() {
		// given
		Long postId = 1L;
		PostSaveRequest request = new PostSaveRequest("", "", "", null, List.of(), null);

		// when, then
		RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/edit/{postId}", String.valueOf(postId))
				.then().log().all()
				.status(HttpStatus.BAD_REQUEST)
				.body("message", containsString("카테고리 선택은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 삭제 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void deletePostSuccessfullyReturns200OK() {
		// given
		Long postId = 1L;
		NoContent mockResponse = NoContent.from(PostStatusCode.POST_DELETE_SUCCESS);

		given(postService.deletePost(eq(postId), any(Member.class))).willReturn(mockResponse);

		// when
		NoContent response = RestAssuredMockMvc.given().log().all()
				.when().patch("/api/posts/remove/{postId}", String.valueOf(postId))
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(NoContent.class);

		// then
		assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
	}

	@Test
	void 프로젝트_게시글_생성_요청이_성공적으로_응답한다() {
		// given
		ProjectPostRequest request = PostRequestFactory.createProjectPostRequest();
		API<Long> mockResponse = API.of(PostStatusCode.PROJECT_POST_CREATE_SUCCESS, 1L);
		given(projectPostService.createProjectPost(any(Member.class), any(ProjectPostRequest.class)))
			.willReturn(mockResponse);

		// when
		API<Long> response = RestAssuredMockMvc.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/project/upload")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
	}

	@Test
	void ProjectPostRequest_Dto에_슬라이드_이미지_정보가_없으면_예외가_발생한다() {
		// given
		ProjectPostRequest request = new ProjectPostRequest("", "", "", 1L, List.of(), List.of(), null);

		// when & then
		RestAssuredMockMvc.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/project/upload")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST)
			.body("code", equalTo(101));
	}

	@Test
	void 프로젝트_게시글_수정_요청이_성공적으로_응답한다() {
		// given
		ProjectPostRequest request = PostRequestFactory.createProjectPostRequest();
		API<Long> mockResponse = API.of(PostStatusCode.PROJECT_POST_UPDATE_SUCCESS, 1L);
		given(projectPostService.updateProjectPost(anyLong(), any(Member.class), any(ProjectPostRequest.class)))
			.willReturn(mockResponse);

		// when
		API<Long> response = RestAssuredMockMvc.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.pathParam("projectId", 1L)
			.when().post("/api/posts/project/edit/{projectId}")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
	}

	@Test
	void 프로젝트_게시글_삭제_요청이_성공적으로_응답한다() {
		// given
		ProjectPostRequest request = PostRequestFactory.createProjectPostRequest();
		NoContent mockResponse = NoContent.from(PostStatusCode.PROJECT_POST_DELETE_SUCCESS);
		given(projectPostService.deletePost(anyLong(), any(Member.class)))
			.willReturn(mockResponse);

		// when
		NoContent response = RestAssuredMockMvc.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.pathParam("projectId", 1L)
			.when().delete("/api/posts/project/remove/{projectId}")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
	}

}

