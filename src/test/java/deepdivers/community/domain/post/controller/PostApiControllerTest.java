package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.hamcrest.Matchers.containsString;

import java.util.List;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.API;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.controller.api.PostApiController;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.service.PostService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = PostApiController.class)
class PostApiControllerTest extends ControllerTest {

	@MockBean
	private PostService postService;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) {
		RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
		mockingAuthArgumentResolver();
	}

	@Test
	@DisplayName("게시글 생성 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void createPostSuccessfullyReturns200OK() {
		// given
		PostSaveRequest request = new PostSaveRequest(
				"Post Title",
				"Post Content",
				1L,
				List.of("tag1", "tag2")
		);
		PostSaveResponse responseBody = new PostSaveResponse(1L);
		API<PostSaveResponse> mockResponse = API.of(PostStatusType.POST_CREATE_SUCCESS, responseBody);

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
				1L,
				List.of("tag1", "tag2")
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
				1L,
				List.of("tag1", "tag2")
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
				null,
				List.of("tag1", "tag2")
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
	@DisplayName("게시글 작성 시 해시태그 없이 작성해도 200 OK를 반환한다")
	void createPostWithoutHashtagsReturns200OK() {
		// given
		PostSaveRequest request = new PostSaveRequest(
				"Post Title",
				"Post Content",
				1L,
				null
		);

		PostSaveResponse responseBody = new PostSaveResponse(1L);
		API<PostSaveResponse> mockResponse = API.of(PostStatusType.POST_CREATE_SUCCESS, responseBody);

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
	@DisplayName("게시글 수정 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void updatePostSuccessfullyReturns200OK() {
		// given
		Long postId = 1L;
		PostSaveRequest request = new PostSaveRequest(
				"Updated Title",
				"Updated Content",
				1L,
				List.of("tag1", "tag2")
		);
		PostSaveResponse responseBody = new PostSaveResponse(postId);
		API<PostSaveResponse> mockResponse = API.of(PostStatusType.POST_UPDATE_SUCCESS, responseBody);

		given(postService.updatePost(eq(postId), any(PostSaveRequest.class), any(Member.class))).willReturn(mockResponse);

		// when
		API<PostSaveResponse> response = RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/update/{postId}", String.valueOf(postId))
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
	void updatePostWithoutTitleReturns400BadRequest() {
		// given
		Long postId = 1L;
		PostSaveRequest request = new PostSaveRequest(
				null,
				"Updated Content",
				1L,
				List.of("tag1", "tag2")
		);

		// when, then
		RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/update/{postId}", String.valueOf(postId))
				.then().log().all()
				.status(HttpStatus.BAD_REQUEST)
				.body("message", containsString("게시글 제목은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 수정 요청에서 카테고리 ID가 없으면 400 BadRequest를 반환한다")
	void updatePostWithoutCategoryIdReturns400BadRequest() {
		// given
		Long postId = 1L;
		PostSaveRequest request = new PostSaveRequest(
				"Updated Title",
				"Updated Content",
				null,
				List.of("tag1", "tag2")
		);

		// when, then
		RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/update/{postId}", String.valueOf(postId))
				.then().log().all()
				.status(HttpStatus.BAD_REQUEST)
				.body("message", containsString("카테고리 선택은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 수정 요청에서 유효하지 않은 ID가 주어지면 400 BAD_REQUEST를 반환한다")
	void updatePostWithInvalidPostIdReturns400BAD_REQUEST() {
		// given
		Long invalidPostId = 999L;
		PostSaveRequest request = new PostSaveRequest(
				"Updated Title",
				"Updated Content",
				1L,
				List.of("tag1", "tag2")
		);

		given(postService.updatePost(eq(invalidPostId), any(PostSaveRequest.class), any(Member.class)))
				.willThrow(new BadRequestException(PostExceptionType.POST_NOT_FOUND));

		// when, then
		RestAssuredMockMvc.given().log().all()
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.when().post("/api/posts/update/{postId}", String.valueOf(invalidPostId))
				.then().log().all()
				.status(HttpStatus.BAD_REQUEST)
				.body("message", containsString("게시글을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("게시글 삭제 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void deletePostSuccessfullyReturns200OK() {
		// given
		Long postId = 1L;
		NoContent mockResponse = NoContent.from(PostStatusType.POST_DELETE_SUCCESS);

		given(postService.deletePost(eq(postId), any(Member.class))).willReturn(mockResponse);

		// when
		NoContent response = RestAssuredMockMvc.given().log().all()
				.when().patch("/api/posts/delete/{postId}", String.valueOf(postId))
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(NoContent.class);

		// then
		assertThat(response).isNotNull();
		assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("존재하지 않는 게시글 ID로 삭제 요청을 하면 400 BAD REQUEST를 반환한다")
	void deletePostWithInvalidPostIdReturns400BADREQUEST() {
		// given
		Long invalidPostId = 999L;

		given(postService.deletePost(eq(invalidPostId), any(Member.class)))
				.willThrow(new BadRequestException(PostExceptionType.POST_NOT_FOUND));

		// when, then
		RestAssuredMockMvc.given().log().all()
				.when().patch("/api/posts/delete/{postId}", String.valueOf(invalidPostId))
				.then().log().all()
				.status(HttpStatus.BAD_REQUEST)
				.body("message", containsString("게시글을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("권한이 없는 사용자가 게시글 삭제 요청을 하면 400 BAD REQUEST을 반환한다")
	void deletePostWithoutPermissionReturns403Forbidden() {
		// given
		Long postId = 1L;

		given(postService.deletePost(eq(postId), any(Member.class)))
				.willThrow(new BadRequestException(PostExceptionType.NOT_POST_AUTHOR));

		// when, then
		RestAssuredMockMvc.given().log().all()
				.when().patch("/api/posts/delete/{postId}", String.valueOf(postId))
				.then().log().all()
				.status(HttpStatus.BAD_REQUEST)
				.body("message", containsString("게시글 작성자가 아닙니다."));
	}

}

