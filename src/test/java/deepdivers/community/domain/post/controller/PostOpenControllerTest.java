package deepdivers.community.domain.post.controller;

import static deepdivers.community.domain.post.dto.response.statustype.PostStatusType.POST_VIEW_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import deepdivers.community.domain.post.controller.dto.GetAllPostsTestResponse;
import java.time.LocalDateTime;
import java.util.List;

import deepdivers.community.domain.post.repository.PostQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.WebApplicationContext;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.controller.open.PostOpenController;
import deepdivers.community.domain.post.dto.response.CountInfo;
import deepdivers.community.domain.post.dto.response.MemberInfo;
import deepdivers.community.domain.post.dto.response.GetAllPostsResponse; // Ensure this is used
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.global.exception.model.BadRequestException;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = PostOpenController.class)
class PostOpenControllerTest extends ControllerTest {

	@MockBean
	private PostService postService;

	@MockBean
	private PostQueryRepository postQueryRepository;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) {
		RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
	}

	@Test
	@DisplayName("게시글 상세 조회 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void getPostByIdSuccessfullyReturns200OK() {
		// given
		Long postId = 1L;
		String clientIp = "127.0.0.1";
		PostReadResponse responseBody = new PostReadResponse(
				postId,
				"Post Title",
				"Post Content",
				"",
				1L,
				new MemberInfo(1L, "작성자 닉네임", "이미지 URL", "개발자"),
				new CountInfo(100, 50, 10),
				List.of("tag1", "tag2"),
				List.of("http/temp/f.jpeg"),
				"2024-09-26T12:00:00"
		);

		API<PostReadResponse> mockResponse = API.of(POST_VIEW_SUCCESS, responseBody);

		given(postService.readPostDetail(eq(postId), eq(clientIp))).willReturn(mockResponse);

		// when
		API<PostReadResponse> response = RestAssuredMockMvc.given().log().all()
				.when().get("/open/posts/{postId}", String.valueOf(postId))
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

	@Test
	@DisplayName("존재하지 않는 게시글 ID로 조회 요청을 하면 400 BAD REQUEST를 반환한다")
	void getPostByInvalidIdReturns400BAD_REQUEST() {
		// given
		Long invalidPostId = 999L;
		String clientIp = "127.0.0.1";

		given(postService.readPostDetail(eq(invalidPostId), eq(clientIp)))
				.willThrow(new BadRequestException(PostExceptionType.POST_NOT_FOUND));

		// when, then
		RestAssuredMockMvc.given().log().all()
				.when().get("/open/posts/{postId}", String.valueOf(invalidPostId))
				.then().log().all()
				.status(HttpStatus.BAD_REQUEST)
				.body("message", containsString("게시글을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("모든 게시글 조회 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void getAllPostsSuccessfullyReturns200OK() {
		// given
		Long lastPostId = 10L;
		Long categoryId = 1L;

		List<GetAllPostsResponse> mockQueryResult = List.of(
			new GetAllPostsResponse(
				1L, "Title 1", "Content 1", "", categoryId,
				new MemberInfo(1L, "Author 1", "author1.png", "Developer"), new CountInfo(10, 5, 2),
				"tag1,tag2", "http/temp/f.jpeg", LocalDateTime.now())
		);

		given(postQueryRepository.findAllPosts(lastPostId, categoryId)).willReturn(mockQueryResult);

		// when
		API<List<GetAllPostsTestResponse>> response = RestAssuredMockMvc.given().log().all()
				.queryParam("categoryId", categoryId)
				.queryParam("lastPostId", lastPostId)
				.when().get("/open/posts")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		GetAllPostsTestResponse mockTestResponse = GetAllPostsTestResponse.from(mockQueryResult.getFirst());
		API<List<GetAllPostsTestResponse>> mockResponse = API.of(POST_VIEW_SUCCESS, List.of(mockTestResponse));

		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

	@Test
	@DisplayName("카테고리 ID나 마지막 게시글 ID 없이 모든 게시글 조회 요청이 성공적으로 처리되면 200 OK를 반환하고 결과를 검증한다")
	void getAllPostsWithoutParamsReturns200OK() {
		// given
		List<GetAllPostsResponse> mockQueryResult = List.of(
			new GetAllPostsResponse(
				1L, "Title 1", "Content 1", "", 1L,
				new MemberInfo(1L, "Author 1", "author1.png", "Developer"), new CountInfo(10, 5, 2),
				"tag1,tag2", "http/temp/f.jpeg", LocalDateTime.now())
		);

		given(postQueryRepository.findAllPosts(null, null)).willReturn(mockQueryResult);

		// when
		API<List<GetAllPostsTestResponse>> response = RestAssuredMockMvc.given().log().all()
				.when().get("/open/posts")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		GetAllPostsTestResponse mockTestResponse = GetAllPostsTestResponse.from(mockQueryResult.getFirst());
		API<List<GetAllPostsTestResponse>> mockResponse = API.of(POST_VIEW_SUCCESS, List.of(mockTestResponse));

		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

	@Test
	@DisplayName("카테고리 ID만 제공된 경우 모든 게시글 조회 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void getAllPostsWithOnlyCategoryIdReturns200OK() {
		// given
		Long categoryId = 1L;

		List<GetAllPostsResponse> mockQueryResult = List.of(
				new GetAllPostsResponse(
						1L, "Title 1", "Content 1", "", categoryId,
						new MemberInfo(1L, "Author 1", "author1.png", "Developer"), new CountInfo(10, 5, 2),
						"tag1,tag2", "http/temp/f.jpeg", LocalDateTime.now())
		);

		given(postQueryRepository.findAllPosts(null, categoryId)).willReturn(mockQueryResult);

		// when
		API<List<GetAllPostsTestResponse>> response = RestAssuredMockMvc.given().log().all()
				.queryParam("categoryId", categoryId)
				.when().get("/open/posts")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		GetAllPostsTestResponse mockTestResponse = GetAllPostsTestResponse.from(mockQueryResult.getFirst());
		API<List<GetAllPostsTestResponse>> mockResponse = API.of(POST_VIEW_SUCCESS, List.of(mockTestResponse));

		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

	@Test
	@DisplayName("마지막 게시글 ID만 제공된 경우 모든 게시글 조회 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void getAllPostsWithOnlyLastPostIdReturns200OK() {
		// given
		Long lastPostId = 10L;

		List<GetAllPostsResponse> mockQueryResult = List.of(
				new GetAllPostsResponse(
						1L, "Title 1", "Content 1", "", 1L,
						new MemberInfo(1L, "Author 1", "author1.png", "Developer"), new CountInfo(10, 5, 2),
						"tag1,tag2", "http/temp/f.jpeg", LocalDateTime.now())
		);

		given(postQueryRepository.findAllPosts(lastPostId, null)).willReturn(mockQueryResult);

		// when
		API<List<GetAllPostsTestResponse>> response = RestAssuredMockMvc.given().log().all()
				.queryParam("lastPostId", lastPostId)
				.when().get("/open/posts")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		GetAllPostsTestResponse mockTestResponse = GetAllPostsTestResponse.from(mockQueryResult.getFirst());
		API<List<GetAllPostsTestResponse>> mockResponse = API.of(POST_VIEW_SUCCESS, List.of(mockTestResponse));

		assertThat(mockResponse).usingRecursiveComparison().isEqualTo(response);
	}

}

