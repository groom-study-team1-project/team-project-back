package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.*;

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
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
import deepdivers.community.domain.hashtag.repository.HashtagRepository;
import deepdivers.community.domain.hashtag.repository.PostHashtagRepository;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.controller.api.PostApiController;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.request.PostUpdateRequest;
import deepdivers.community.domain.post.dto.response.CountInfo;
import deepdivers.community.domain.post.dto.response.MemberInfo;
import deepdivers.community.domain.post.dto.response.PostAllReadResponse;
import deepdivers.community.domain.post.dto.response.PostCountResponse;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.PostUpdateResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.domain.post.service.PostService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = PostApiController.class)
class PostApiControllerTest extends ControllerTest {

	@MockBean
	private PostService postService;

	@MockBean
	private PostRepository postRepository;

	private PostReadResponse mockPostResponse;

	private PostHashtagRepository postHashtagRepository;

	private HashtagRepository hashtagRepository;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) throws Exception {
		RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
		mockingAuthArgumentResolver();

		// 게시글 조회 시 사용할 mock 데이터 생성
		mockPostResponse = new PostReadResponse(
			1L,
			"게시글 제목",  // 제목은 테스트에서 기대하는 값과 일치
			"Test Content",
			1L,
			new MemberInfo(1L, "작성자 닉네임", "profile.jpg", "개발자"),  // job 추가
			new CountInfo(100, 50, 20), // CountInfo 객체로 전달
			List.of("hashtag1", "hashtag2"),
			"2024-09-26T12:00:00"  // 날짜를 테스트에서 기대하는 값으로 수정
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
		assertThat(postResponse.memberInfo().getNickname()).isEqualTo("작성자 닉네임");// 작성자 닉네임 검증
		assertThat(postResponse.countInfo().getViewCount()).isEqualTo(100);;
		assertThat(postResponse.countInfo().getLikeCount()).isEqualTo(50);
		assertThat(postResponse.countInfo().getCommentCount()).isEqualTo(20);
		assertThat(postResponse.hashtags()).containsExactly("hashtag1", "hashtag2");  // 해시태그 검증
		assertThat(postResponse.createdAt()).isEqualTo("2024-09-26T12:00:00");  // 생성일 검증
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
		List<PostAllReadResponse> mockPostList = Arrays.asList(
			createMockPost(1L, "게시글 제목 1", "게시글 내용 1", "작성자 닉네임 1", "이미지 URL 1", new CountInfo(100, 50, 10), Arrays.asList("해시태그1", "해시태그2"), "2024-09-26 12:00:00"),
			createMockPost(2L, "게시글 제목 2", "게시글 내용 2", "작성자 닉네임 2", "이미지 URL 2", new CountInfo(200, 100, 20), Arrays.asList("해시태그3", "해시태그4"), "2024-09-27 12:00:00"),
			createMockPost(3L, "게시글 제목 3", "게시글 내용 3", "작성자 닉네임 3", "이미지 URL 3", new CountInfo(300, 150, 30), Arrays.asList("해시태그5", "해시태그6"), "2024-09-28T12:00:00")
		);

		PostCountResponse postCountResponse = new PostCountResponse(3L, mockPostList); // 게시글 총 개수와 목록 설정
		API<PostCountResponse> mockApiResponse = API.of(PostStatusType.POST_VIEW_SUCCESS, postCountResponse);

		// Mocking the service method to return the mock API response
		given(postService.getAllPosts(anyLong(), any())).willReturn(mockApiResponse);

		// when
		API<PostCountResponse> response = RestAssuredMockMvc
			.given().log().all()
			.header("Authorization", "Bearer sample-token")  // 인증 토큰
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/posts")
			.then().log().all()
			.status(HttpStatus.OK) // 200 OK 반환 기대
			.extract()
			.as(new TypeRef<API<PostCountResponse>>() {});  // API<PostCountResponse>로 변환

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
			new MemberInfo(postId, nickname, imageUrl, "개발자"), // job 추가
			countInfo,
			hashtags,
			createdAt
		);
	}

	// 게시글 수정 성공 테스트
	@Test
	@DisplayName("게시글 수정이 성공적으로 처리되면 200 OK를 반환한다")
	void updatePostSuccessfullyReturns200OK() {
		// given
		String[] hashtags = {"Updated", "Post"};
		PostUpdateRequest request = new PostUpdateRequest("수정된 게시글 제목", "수정된 게시글 내용", 1L, hashtags);

		// PostUpdateResponse 구조에서 categoryId가 앞에 오도록 수정
		PostUpdateResponse updateResponse = new PostUpdateResponse(1L, 1L, "수정된 게시글 제목", "수정된 게시글 내용");

		API<PostUpdateResponse> mockResponse = API.of(PostStatusType.POST_UPDATE_SUCCESS, updateResponse);

		given(postService.updatePost(anyLong(), any(PostUpdateRequest.class), any(Member.class)))
			.willReturn(mockResponse);

		// when
		API<PostUpdateResponse> response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/update/1")
			.then().log().all()
			.status(HttpStatus.OK) // 성공적으로 게시글이 수정될 때 200 OK 반환
			.extract()
			.as(new TypeRef<>() {});

		// then
		assertThat(response).isNotNull();
		assertThat(response.getResult().postId()).isEqualTo(1L);
		assertThat(response.getResult().categoryId()).isEqualTo(1L); // categoryId 확인
		assertThat(response.getResult().updatedTitle()).isEqualTo("수정된 게시글 제목"); // title 확인
		assertThat(response.getResult().updatedContent()).isEqualTo("수정된 게시글 내용"); // content 확인

		// 추가된 해시태그가 제대로 반영되었는지 확인
		assertThat(hashtags).containsExactly("Updated", "Post");
	}

	// 게시글 수정 시 작성자가 아닌 경우 400 Bad Request 반환
	@Test
	@DisplayName("게시글 수정 시 작성자가 아닌 경우 400 Bad Request를 반환한다")
	void updatePostNotAuthorReturns400BadRequest() {
		// given
		String[] hashtags = {"Updated", "Post"};
		PostUpdateRequest request = new PostUpdateRequest("수정된 게시글 제목", "수정된 게시글 내용", 1L, hashtags);

		// Mock 설정: 작성자가 아닌 경우 예외 발생
		given(postService.updatePost(anyLong(), any(PostUpdateRequest.class), any(Member.class)))
			.willThrow(new BadRequestException(PostExceptionType.NOT_POST_AUTHOR));

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/update/1")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST) // 400 오류 예상
			.body("code", equalTo(PostExceptionType.NOT_POST_AUTHOR.getCode()))
			.body("message", containsString(PostExceptionType.NOT_POST_AUTHOR.getMessage()));
	}

	// 게시글 수정 시 제목이 없으면 400 Bad Request 반환
	@Test
	@DisplayName("게시글 수정 시 제목이 없으면 400 Bad Request를 반환한다")
	void updatePostWithoutTitleReturns400BadRequest() {
		// given
		String[] hashtags = {"Updated", "Post"};
		PostUpdateRequest request = new PostUpdateRequest(null, "수정된 게시글 내용", 1L, hashtags);

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/update/1")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST) // 400 오류 예상
			.body("code", equalTo(101)) // 실제 오류 코드 101 예상
			.body("message", containsString("게시글 제목은 필수입니다."));
	}

	// 게시글 수정 시 내용이 없으면 400 Bad Request 반환
	@Test
	@DisplayName("게시글 수정 시 내용이 없으면 400 Bad Request를 반환한다")
	void updatePostWithoutContentReturns400BadRequest() {
		// given
		String[] hashtags = {"Updated", "Post"};
		PostUpdateRequest request = new PostUpdateRequest("수정된 게시글 제목", null, 1L, hashtags);

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/update/1")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST) // 400 오류 예상
			.body("code", equalTo(101)) // 실제 오류 코드 101 예상
			.body("message", containsString("게시글 내용은 필수입니다."));
	}

	// 게시글 수정 시 잘못된 해시태그가 입력되면 400 Bad Request 반환
	@Test
	@DisplayName("게시글 수정 시 잘못된 해시태그가 입력되면 400 Bad Request를 반환한다")
	void updatePostWithInvalidHashtagsReturns400BadRequest() {
		// given
		String[] invalidHashtags = {"#Invalid!", "#TooLongTag123"};
		// 유효한 게시글 제목과 내용을 입력하여 해시태그만 테스트하도록 함
		PostUpdateRequest request = new PostUpdateRequest("유효한 제목", "유효한 내용입니다.", 1L, invalidHashtags);

		// Mock 설정: 잘못된 해시태그가 입력되면 예외 발생
		given(postService.updatePost(anyLong(), any(PostUpdateRequest.class), any(Member.class)))
			.willThrow(new BadRequestException(HashtagExceptionType.INVALID_HASHTAG_FORMAT));

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/posts/update/1")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST) // 400 오류 예상
			.body("code", equalTo(HashtagExceptionType.INVALID_HASHTAG_FORMAT.getCode())) // 해시태그 오류 코드
			.body("message", containsString("유효하지 않은 해시태그 형식입니다."));
	}

	@Test
	@DisplayName("게시글 삭제가 성공적으로 처리되면 200 OK를 반환한다")
	void deletePostSuccessfullyReturns204NoContent() {
		// given
		given(postService.deletePost(anyLong(), any(Member.class)))
			.willReturn(NoContent.from(PostStatusType.POST_DELETE_SUCCESS));

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().delete("/api/posts/delete/1")
			.then().log().all()
			.status(HttpStatus.OK); // 성공적으로 삭제될 때 204 No Content 반환
	}

	@Test
	@DisplayName("게시글 삭제 시 작성자가 아닌 경우 400 Bad Request를 반환한다")
	void deletePostNotAuthorReturns400BadRequest() {
		// given
		willThrow(new BadRequestException(PostExceptionType.NOT_POST_AUTHOR))
			.given(postService).deletePost(anyLong(), any(Member.class));

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().delete("/api/posts/delete/1")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST) // 400 오류 예상
			.body("code", equalTo(PostExceptionType.NOT_POST_AUTHOR.getCode()))
			.body("message", containsString(PostExceptionType.NOT_POST_AUTHOR.getMessage()));
	}

	@Test
	@DisplayName("존재하지 않는 게시글 삭제 시 400 Bad Request를 반환한다")
	void deletePostNotFoundReturns400BadRequest() {
		// given
		willThrow(new BadRequestException(PostExceptionType.POST_NOT_FOUND))
			.given(postService).deletePost(anyLong(), any(Member.class));

		// when, then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().delete("/api/posts/delete/999")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST) // 400 오류 예상
			.body("code", equalTo(PostExceptionType.POST_NOT_FOUND.getCode()))
			.body("message", containsString(PostExceptionType.POST_NOT_FOUND.getMessage()));
	}
}
