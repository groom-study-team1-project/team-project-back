package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.util.List;

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
		mockingAuthArgumentResolver(); // Auth Argument Resolver 모킹
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
		PostSaveResponse responseBody = new PostSaveResponse(1L, "Post Title", "Post Content");
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
}

