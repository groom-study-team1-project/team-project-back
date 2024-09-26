package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.response.MemberInfo;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.global.exception.model.BadRequestException;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = PostApiController.class)
class PostApiControllerTest extends ControllerTest {

	@MockBean
	private PostService postService; // PostService를MockBean으로 선언

	private PostReadResponse mockPostResponse;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) throws Exception {
		RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
		mockingAuthArgumentResolver();

		// 게시글 조회 시 사용할 mock 데이터 생성
		mockPostResponse = new PostReadResponse(
			1L,                    // postId
			"게시글 제목",            // title
			"게시글 내용",            // content
			1L,                    // categoryId (예시로 ID 추가)
			new MemberInfo(        // 작성자 정보
				1L,                // memberId
				"작성자 닉네임",       // nickname
				"작성자 이미지 URL"   // imageUrl
			),
			100,                   // viewCount
			10,                    // likeCount
			Arrays.asList("해시태그1", "해시태그2"), // 해시태그 추가
			"2024-09-26T12:00:00" // createdAt (예시)
		);
	}

	@Test
	@DisplayName("게시글 작성이 성공적으로 처리되면 200 OK를 반환한다")
	void createPostSuccessfullyReturns200OK() {
		// given
		String[] hashtags = {"#Spring", "#Boot", "#해시태그"};
		PostCreateRequest request = new PostCreateRequest("게시글 테스트 제목", "게시글 테스트 내용", 1L, hashtags); // 10자 이상의 내용 입력
		PostCreateResponse createResponse = new PostCreateResponse(1L);
		API<PostCreateResponse> mockResponse = API.of(PostStatusType.POST_CREATE_SUCCESS, createResponse);

		given(postService.createPost(any(PostCreateRequest.class), any(Member.class))).willReturn(mockResponse);

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
		PostCreateRequest request = new PostCreateRequest(null, "게시글 내용", 1L, hashtags);

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
		PostCreateRequest request = new PostCreateRequest("게시글 제목", null, 1L, hashtags);

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
		PostCreateRequest request = new PostCreateRequest("게시글 제목", "게시글 내용입니다", null, hashtags); // categoryId가null

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
		PostCreateRequest request = new PostCreateRequest("게시글 제목", "게시글 내용입니다.", 1L, emptyHashtags); // 빈 해시태그 배열로 요청

		PostCreateResponse createResponse = new PostCreateResponse(1L);
		API<PostCreateResponse> mockResponse = API.of(PostStatusType.POST_CREATE_SUCCESS, createResponse);

		given(postService.createPost(any(PostCreateRequest.class), any(Member.class))).willReturn(mockResponse);

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
		PostCreateRequest request = new PostCreateRequest("게시글 제목", "게시글 내용입니다", 1L, invalidHashtags);

		// Mock 설정: 잘못된 해시태그가 입력되면 예외 발생
		given(postService.createPost(any(PostCreateRequest.class), any(Member.class)))
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

	@Test
	@DisplayName("게시글 조회가 성공적으로 처리되면 200 OK와 게시글 정보를 반환한다")
	void getPostByIdSuccessfullyReturns200OK() {
		// given
		given(postService.getPostById(anyLong(), ArgumentMatchers.anyString())).willReturn(mockPostResponse);

		// when
		API<PostReadResponse> response = RestAssuredMockMvc
			.given().log().all()
			.header("Authorization", "Bearer sample-token")  // 인증 토큰
			.header("X-Forwarded-For", "127.0.0.1")  // IP 주소
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/posts/1")
			.then().log().all()
			.status(HttpStatus.OK) // 200 OK 반환 기대
			.extract()
			.as(new TypeRef<API<PostReadResponse>>() {});  // API<PostReadResponse>로 직접 변환

		// then
		PostReadResponse postResponse = response.getResult();  // getResult()로 PostReadResponse 추출
		assertThat(postResponse).isNotNull();
		assertThat(postResponse.title()).isEqualTo("게시글 제목");
		assertThat(postResponse.categoryId()).isEqualTo(1L); // categoryId 검증
		assertThat(postResponse.memberInfo().nickname()).isEqualTo("작성자 닉네임"); // 작성자 닉네임 검증
		assertThat(postResponse.viewCount()).isEqualTo(100);
		assertThat(postResponse.hashtags()).containsExactly("해시태그1", "해시태그2"); // 해시태그 검증
		assertThat(postResponse.createdAt()).isEqualTo("2024-09-26T12:00:00"); // 생성일 검증
	}

	@Test
	@DisplayName("존재하지 않는 게시글 조회 시 400 Bad Request를 반환한다")
	void getPostByIdNotFoundReturns400() {
		// given
		given(postService.getPostById(anyLong(), ArgumentMatchers.anyString()))
			.willThrow(new BadRequestException(PostExceptionType.POST_NOT_FOUND));

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.header("Authorization", "Bearer sample-token")  // 인증 토큰
			.header("X-Forwarded-For", "127.0.0.1")  // IP 주소
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/posts/999")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST) // 400 Bad Request 기대
			.body("code", equalTo(PostExceptionType.POST_NOT_FOUND.getCode()))
			.body("message", equalTo(PostExceptionType.POST_NOT_FOUND.getMessage()));
	}

	@Test
	@DisplayName("회원이 전체 게시글 조회에 성공하면 200 OK와 게시글 목록을 반환한다")
	void getAllPostsSuccessfullyReturns200OK() {
		// given
		List<PostReadResponse> mockPostResponses = Arrays.asList(mockPostResponse, mockPostResponse); // 여러 게시글을 생성
		API<List<PostReadResponse>> mockResponse = API.of(PostStatusType.POST_VIEW_SUCCESS, mockPostResponses);

		given(postService.getAllPosts()).willReturn(mockPostResponses);

		// when
		API<List<PostReadResponse>> response = RestAssuredMockMvc
			.given().log().all()
			.header("Authorization", "Bearer sample-token")  // 인증 토큰
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/posts")
			.then().log().all()
			.status(HttpStatus.OK) // 200 OK 반환 기대
			.extract()
			.as(new TypeRef<API<List<PostReadResponse>>>() {});  // API<List<PostReadResponse>>로 변환

		// then
		assertThat(response.getResult()).hasSize(2);  // 2개의 게시글이 반환됨을 확인
		assertThat(response.getResult().get(0).title()).isEqualTo("게시글 제목");
		assertThat(response.getResult().get(0).hashtags()).containsExactly("해시태그1", "해시태그2");
	}
}
