package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.controller.open.PostOpenController;
import deepdivers.community.domain.post.dto.response.CountInfo;
import deepdivers.community.domain.post.dto.response.MemberInfo;
import deepdivers.community.domain.post.dto.response.PostAllReadResponse; // Ensure this is used
import deepdivers.community.domain.post.dto.response.PostCountResponse;
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

	private MockMvc mockMvc; // MockMvc 필드 선언
	private PostReadResponse mockPostResponse;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		RestAssuredMockMvc.mockMvc(mockMvc);

		// 게시글 조회 시 사용할 mock 데이터 생성
		mockPostResponse = new PostReadResponse(
			1L,                    // postId
			"게시글 제목",            // title
			"게시글 내용",            // content
			1L,                    // categoryId (예시로 ID 추가)
			new MemberInfo(1L, "작성자 닉네임", "이미지 URL", "개발자"), // memberJob 추가
			new CountInfo(100, 50, 10), // countInfo
			Arrays.asList("해시태그1", "해시태그2"), // 해시태그 추가
			"2024-09-26T12:00:00" // createdAt (예시)
		);
	}

	// 비회원 게시글 조회 성공 테스트
	@Test
	@DisplayName("비회원 게시글 조회가 성공적으로 처리되면 200 OK와 게시글 정보를 반환한다")
	void getPostByIdSuccessfullyReturns200OKForOpen() {
		// given
		given(postService.readPostDetail(anyLong(), anyString())).willReturn(mockPostResponse);

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
		assertThat(postResponse.memberInfo().getNickname()).isEqualTo("작성자 닉네임");// 작성자 닉네임 검증
		assertThat(postResponse.countInfo().getViewCount()).isEqualTo(100);
		assertThat(postResponse.countInfo().getLikeCount()).isEqualTo(50);
		assertThat(postResponse.countInfo().getCommentCount()).isEqualTo(10);
		assertThat(postResponse.hashtags()).containsExactly("해시태그1", "해시태그2"); // 해시태그 검증
		assertThat(postResponse.createdAt()).isEqualTo("2024-09-26T12:00:00"); // 생성일 검증
	}

	// 존재하지 않는 게시글 조회 테스트
	@Test
	@DisplayName("존재하지 않는 게시글 조회 시 400 Bad Request를 반환한다")
	void getPostByIdNotFoundReturns400ForOpen() {
		// given
		given(postService.readPostDetail(anyLong(), anyString()))
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
	@DisplayName("전체 게시글 조회가 성공적으로 처리되면 200 OK와 게시글 목록을 반환한다")
	void getAllPostsSuccessfullyReturns200OK() {
		// given
		List<PostAllReadResponse> mockPostList = Arrays.asList(
			createMockPost(1L, "게시글 제목 1", "게시글 내용 1", "작성자 닉네임 1", "이미지 URL 1", new CountInfo(100, 50, 10), Arrays.asList("해시태그1", "해시태그2"), "2024-09-26T12:00:00"),
			createMockPost(2L, "게시글 제목 2", "게시글 내용 2", "작성자 닉네임 2", "이미지 URL 2", new CountInfo(200, 100, 20), Arrays.asList("해시태그3", "해시태그4"), "2024-09-27T12:00:00"),
			createMockPost(3L, "게시글 제목 3", "게시글 내용 3", "작성자 닉네임 3", "이미지 URL 3", new CountInfo(300, 150, 30), Arrays.asList("해시태그5", "해시태그6"), "2024-09-28T12:00:00")
		);

		PostCountResponse postCountResponse = new PostCountResponse(3L, mockPostList); // 게시글 총 개수와 목록 설정
		API<PostCountResponse> mockApiResponse = API.of(PostStatusType.POST_VIEW_SUCCESS, postCountResponse);

		// Mocking the service method to return the mock API response
		given(postService.getAllPosts(anyLong(), any())).willReturn(mockApiResponse);

		// when
		API<PostCountResponse> response = RestAssuredMockMvc
			.given().log().all()
			.header("X-Forwarded-For", "127.0.0.1")  // IP 주소
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/open/posts") // Assuming the endpoint for all posts is "/open/posts"
			.then().log().all()
			.status(HttpStatus.OK) // 200 OK 반환 기대
			.extract()
			.as(new TypeRef<API<PostCountResponse>>() {});  // API<PostCountResponse>로 직접 변환

		// then
		PostCountResponse postResponses = response.getResult();  // getResult()로 PostCountResponse 추출
		assertThat(postResponses).isNotNull();
		assertThat(postResponses.getPosts().size()).isEqualTo(3); // Verify size of the list is now 3

		// Verify content of the posts
		assertThat(postResponses.getPosts().get(0).getTitle()).isEqualTo("게시글 제목 1");
		assertThat(postResponses.getPosts().get(1).getTitle()).isEqualTo("게시글 제목 2");
		assertThat(postResponses.getPosts().get(2).getTitle()).isEqualTo("게시글 제목 3");
	}

	// Mock 데이터 생성 메서드
	private PostAllReadResponse createMockPost(Long postId, String title, String content, String nickname, String imageUrl, CountInfo countInfo, List<String> hashtags, String createdAt) {
		return new PostAllReadResponse(
			postId,
			title,
			content,
			1L, // categoryId (예시로 고정)
			new MemberInfo(postId, nickname, imageUrl, "개발자"), // memberJob 추가
			countInfo,
			hashtags,
			createdAt
		);
	}
}
