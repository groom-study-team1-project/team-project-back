package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

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
import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.controller.api.PostApiController;
import deepdivers.community.domain.post.dto.request.PostRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.global.exception.model.BadRequestException;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = PostApiController.class)
class PostControllerTest extends ControllerTest {

	@MockBean
	private PostService postService; // PostService를MockBean으로 선언

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) throws Exception {
		RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
		mockingAuthArgumentResolver();
	}

	@Test
	@DisplayName("게시글 작성이 성공적으로 처리되면 200 OK를 반환한다")
	void createPostSuccessfullyReturns200OK() {
		// given
		String[] hashtags = {"#Spring", "#Boot", "#해시태그"};
		PostRequest request = new PostRequest("게시글 테스트 제목", "게시글 테스트 내용", 1L, hashtags); // 10자 이상의 내용 입력
		PostCreateResponse createResponse = new PostCreateResponse(1L);
		API<PostCreateResponse> mockResponse = API.of(PostStatusType.POST_CREATE_SUCCESS, createResponse);

		given(postService.createPost(any(PostRequest.class), any(Member.class))).willReturn(mockResponse);

		// when
		API<PostCreateResponse> response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/upload")
			.then().log().all()
			.status(HttpStatus.OK) // 성공적으로 게시글이 작성될 때 200 OK 반환
			.extract()
			.as(new TypeRef<>() {});

		// then
		assertThat(response).isNotNull();
		assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
	}


	@Test
	@DisplayName("게시글 작성 시 제목이 없으면 400 Bad Request 를 반환한다")
	void createPostWithoutTitleReturns400BadRequest() {
		// given
		String[] hashtags = {"#Spring", "#Boot"};
		PostRequest request = new PostRequest(null, "게시글 내용", 1L, hashtags);

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/upload")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST)
			.body("code", equalTo(101))
			.body("message", containsString("게시글 제목은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 작성 시 내용이 없으면 400 Bad Request 를 반환한다")
	void createPostWithoutContentReturns400BadRequest() {
		// given
		String[] hashtags = {"#Spring", "#Boot"};
		PostRequest request = new PostRequest("게시글 제목", null, 1L, hashtags);

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/upload")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST)
			.body("code", equalTo(101))
			.body("message", containsString("게시글 내용은 필수입니다."));
	}

	@Test
	@DisplayName("게시글 작성 시 카테고리 정보가 없으면 400 Bad Request를 반환한다")
	void createPostWithoutCategoryReturns400BadRequest() {
		// given
		String[] hashtags = {"#Spring", "#Boot"};
		PostRequest request = new PostRequest("게시글 제목", "게시글 내용입니다", null, hashtags); // categoryId가null

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/upload")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST) // 400 오류 예상
			.body("code", equalTo(101)) // 실제 오류 코드101을 기대
			.body("message", containsString("카테고리 선택은 필수입니다."));
	}


	@Test
	@DisplayName("게시글 작성 시 해시태그 없이 작성해도 200 OK를 반환한다")
	void createPostWithoutHashtagsReturns200OK() {
		// given
		String[] emptyHashtags = {}; // 빈 배열로 설정
		PostRequest request = new PostRequest("게시글 제목", "게시글 내용입니다.", 1L, emptyHashtags); // 빈 해시태그 배열로 요청

		PostCreateResponse createResponse = new PostCreateResponse(1L);
		API<PostCreateResponse> mockResponse = API.of(PostStatusType.POST_CREATE_SUCCESS, createResponse);

		given(postService.createPost(any(PostRequest.class), any(Member.class))).willReturn(mockResponse);

		// when
		API<PostCreateResponse> response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/upload")
			.then().log().all()
			.status(HttpStatus.OK) // 성공적으로 게시글이 작성될 때200 OK 반환
			.extract()
			.as(new TypeRef<>() {});

		// then
		assertThat(response).isNotNull();
		assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
	}

	@Test
	@DisplayName("게시글 작성 시 잘못된 해시태그가 입력되면 400 Bad Request를 반환한다")
	void createPostWithInvalidHashtagsReturns400BadRequest() {
		// given
		String[] invalidHashtags = {"Spring", "#Invalid!", "#TooLongTag123"};
		PostRequest request = new PostRequest("게시글 제목", "게시글 내용입니다", 1L, invalidHashtags);

		// Mock 설정: 잘못된 해시태그가 입력되면 예외 발생
		given(postService.createPost(any(PostRequest.class), any(Member.class)))
			.willThrow(new BadRequestException(HashtagExceptionType.INVALID_HASHTAG_FORMAT));

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/upload")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST) // 400 오류 예상
			.body("code", equalTo(3302)) // 해시태그 오류 코드
			.body("message", containsString("유효하지 않은 해시태그 형식입니다."));
	}


}
