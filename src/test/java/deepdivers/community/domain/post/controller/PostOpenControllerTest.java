package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

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
import deepdivers.community.domain.post.controller.open.PostOpenController;
import deepdivers.community.domain.post.dto.response.MemberInfo;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.global.exception.model.BadRequestException;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = PostOpenController.class)
class PostOpenControllerTest extends ControllerTest {

	@MockBean
	private PostService postService; // PostService를 MockBean으로 선언

	private PostReadResponse mockPostResponse;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) throws Exception {
		RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

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

	// 비회원 게시글 조회 성공 테스트
	@Test
	@DisplayName("비회원 게시글 조회가 성공적으로 처리되면 200 OK와 게시글 정보를 반환한다")
	void getPostByIdSuccessfullyReturns200OKForOpen() {
		// given
		given(postService.getPostById(anyLong(), ArgumentMatchers.anyString())).willReturn(mockPostResponse);

		// when
		API<PostReadResponse> response = RestAssuredMockMvc
			.given().log().all()
			.header("X-Forwarded-For", "127.0.0.1")  // IP 주소
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/open/posts/1")
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

	// 존재하지 않는 게시글 조회 테스트
	@Test
	@DisplayName("존재하지 않는 게시글 조회 시 400 Bad Request를 반환한다")
	void getPostByIdNotFoundReturns400ForOpen() {
		// given
		given(postService.getPostById(anyLong(), ArgumentMatchers.anyString()))
			.willThrow(new BadRequestException(PostExceptionType.POST_NOT_FOUND));

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.header("X-Forwarded-For", "127.0.0.1")  // IP 주소
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/open/posts/999")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST) // 400 Bad Request 기대
			.body("code", equalTo(PostExceptionType.POST_NOT_FOUND.getCode()))
			.body("message", equalTo(PostExceptionType.POST_NOT_FOUND.getMessage()));
	}

	@Test
	@DisplayName("비회원이 전체 게시글 조회에 성공하면 200 OK와 게시글 목록을 반환한다")
	void getAllPostsSuccessfullyReturns200OKForOpen() {
		// given
		List<PostReadResponse> mockPostResponses = Arrays.asList(mockPostResponse, mockPostResponse); // 여러 게시글을 생성
		API<List<PostReadResponse>> mockResponse = API.of(PostStatusType.POST_VIEW_SUCCESS, mockPostResponses);

		given(postService.getAllPosts()).willReturn(mockPostResponses);

		// when
		API<List<PostReadResponse>> response = RestAssuredMockMvc
			.given().log().all()
			.header("X-Forwarded-For", "127.0.0.1")  // IP 주소
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/open/posts")
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
