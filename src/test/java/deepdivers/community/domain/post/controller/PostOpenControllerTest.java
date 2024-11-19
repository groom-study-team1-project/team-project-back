package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

import deepdivers.community.domain.post.repository.PostQueryRepository;
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
				1L,
				new MemberInfo(1L, "작성자 닉네임", "이미지 URL", "개발자"),
				new CountInfo(100, 50, 10),
				List.of("tag1", "tag2"),
				"2024-09-26T12:00:00"
		);
		API<PostReadResponse> mockResponse = API.of(PostStatusType.POST_VIEW_SUCCESS, responseBody);

		given(postService.readPostDetail(eq(postId), eq(clientIp))).willReturn(responseBody);

		// when
		API<PostReadResponse> response = RestAssuredMockMvc.given().log().all()
				.when().get("/open/posts/{postId}", postId)
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		PostReadResponse postResponse = response.getResult();
		assertThat(postResponse).isNotNull();
		assertThat(postResponse.title()).isEqualTo("게시글 제목");
		assertThat(postResponse.categoryId()).isEqualTo(1L);
		assertThat(postResponse.memberInfo().getNickname()).isEqualTo("작성자 닉네임");
		assertThat(postResponse.countInfo().getViewCount()).isEqualTo(100);
		assertThat(postResponse.countInfo().getLikeCount()).isEqualTo(50);
		assertThat(postResponse.countInfo().getCommentCount()).isEqualTo(10);
		assertThat(postResponse.hashtags()).containsExactly("해시태그1", "해시태그2");
		assertThat(postResponse.createdAt()).isEqualTo("2024-09-26T12:00:00");
	}

	@Test
	@DisplayName("존재하지 않는 게시글 ID로 조회 요청을 하면 404 Not Found를 반환한다")
	void getPostByInvalidIdReturns404NotFound() {
		// given
		Long invalidPostId = 999L;
		String clientIp = "127.0.0.1";

		given(postService.readPostDetail(eq(invalidPostId), eq(clientIp)))
				.willThrow(new BadRequestException(PostExceptionType.POST_NOT_FOUND));

		// when, then
		RestAssuredMockMvc.given().log().all()
				.when().get("/open/posts/{postId}", invalidPostId)
				.then().log().all()
				.status(HttpStatus.NOT_FOUND)
				.body("message", containsString("게시글을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("모든 게시글 조회 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
	void getAllPostsSuccessfullyReturns200OK() {
		// given
		Long lastPostId = 10L;
		Long categoryId = 1L;

		List<PostAllReadResponse> responseList = List.of(
				new PostAllReadResponse(
						1L,
						"Title 1",
						"Content 1",
						categoryId,
						new MemberInfo(1L, "Author 1", "author1.png", "Developer"),
						new CountInfo(10, 5, 2),
						List.of("tag1", "tag2"),
						"2023-11-15 12:00:00"
				),
				new PostAllReadResponse(
						2L,
						"Title 2",
						"Content 2",
						categoryId,
						new MemberInfo(2L, "Author 2", "author2.png", "Designer"),
						new CountInfo(20, 15, 5),
						List.of("tag3"),
						"2023-11-15 13:00:00"
				)
		);

		given(postQueryRepository.findAllPosts(eq(lastPostId), eq(categoryId))).willReturn(responseList);

		// when
		API<List<PostAllReadResponse>> response = RestAssuredMockMvc.given().log().all()
				.queryParam("categoryId", categoryId)
				.queryParam("lastPostId", lastPostId)
				.when().get("/open/posts")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		List<PostAllReadResponse> posts = response.getResult();
		assertThat(posts).isNotNull();
		assertThat(posts).hasSize(2);
	}


	@Test
	@DisplayName("카테고리 ID나 마지막 게시글 ID 없이 게시글 조회 요청이 성공적으로 처리되면 200 OK를 반환하고 결과를 검증한다")
	void getAllPostsWithoutParamsReturns200OK() {
		// given
		List<PostAllReadResponse> responseList = List.of(
				new PostAllReadResponse(
						1L,
						"Title 1",
						"Content 1",
						1L,
						new MemberInfo(1L, "Author 1", "author1.png", "Developer"),
						new CountInfo(10, 5, 2),
						List.of("tag1", "tag2"),
						"2024-09-26T12:00:00"
				)
		);

		given(postQueryRepository.findAllPosts(null, null)).willReturn(responseList);

		// when
		API<List<PostAllReadResponse>> response = RestAssuredMockMvc.given().log().all()
				.when().get("/open/posts")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		List<PostAllReadResponse> posts = response.getResult();
		assertThat(posts).isNotNull();
		assertThat(posts).hasSize(1);

		PostAllReadResponse post = posts.get(0);
		assertThat(post.getPostId()).isEqualTo(1L);
		assertThat(post.getTitle()).isEqualTo("Title 1");
		assertThat(post.getContent()).isEqualTo("Content 1");
		assertThat(post.getCategoryId()).isEqualTo(1L);
		assertThat(post.getMemberInfo().getNickname()).isEqualTo("Author 1");
		assertThat(post.getCountInfo().getViewCount()).isEqualTo(10);
		assertThat(post.getCountInfo().getLikeCount()).isEqualTo(5);
		assertThat(post.getCountInfo().getCommentCount()).isEqualTo(2);
		assertThat(post.getHashtags()).containsExactly("tag1", "tag2");
		assertThat(post.getCreatedAt()).isEqualTo("2024-09-26T12:00:00");
	}

	@Test
	@DisplayName("게시글 조회 요청 시 데이터베이스에 데이터가 없을 경우 빈 목록을 반환한다")
	void getAllPostsWithNoDataReturnsEmptyList() {
		// given
		given(postQueryRepository.findAllPosts(null, null)).willReturn(List.of());

		// when
		API<List<PostAllReadResponse>> response = RestAssuredMockMvc.given().log().all()
				.when().get("/open/posts")
				.then().log().all()
				.status(HttpStatus.OK)
				.extract()
				.as(new TypeRef<>() {
				});

		// then
		List<PostAllReadResponse> posts = response.getResult();
		assertThat(posts).isNotNull();
		assertThat(posts).isEmpty();
	}

}

