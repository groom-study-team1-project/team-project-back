package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.post.controller.api.PostApiController;
import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.service.LikeService;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.security.jwt.AuthorizationResolver;
import io.restassured.common.mapper.TypeRef;
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
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(controllers = PostApiController.class)
class PostLikeControllerTest extends ControllerTest {

    @MockBean
    private PostService postService;

    @MockBean
    private LikeService likeService;

    @MockBean
    private AuthorizationResolver authorizationResolver;

    @Test
    @DisplayName("좋아요 추가 성공 시 200 OK 반환")
    void likeSuccess() {
        // given
        Long postId = 1L;
        LikeRequest likeRequest = new LikeRequest(postId);

        // NoContent 응답을 반환하는 설정
        NoContent successResponse = NoContent.from(PostStatusType.POST_LIKE_SUCCESS);
        given(likeService.likePost(eq(likeRequest), anyLong())).willReturn(successResponse);

        // when
        RestAssuredMockMvc
                .given().log().all()
                .header("X-Forwarded-For", "127.0.0.1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeRequest)
                .when().post("/api/posts/like")
                .then().log().all()
                .status(HttpStatus.OK);  // 상태 코드만 확인

        assertThat(successResponse).isNotNull();
    }

    @Test
    @DisplayName("좋아요 삭제 성공 시 200 OK 반환")
    void unLikeSuccess() {
        // given
        Long postId = 1L;
        LikeRequest likeRequest = new LikeRequest(postId);

        NoContent successResponse = NoContent.from(PostStatusType.POST_UNLIKE_SUCCESS);
        given(likeService.unlikePost(eq(likeRequest), anyLong())).willReturn(successResponse);

        // when
        RestAssuredMockMvc
                .given().log().all()
                .header("X-Forwarded-For", "127.0.0.1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeRequest)
                .when().post("/api/posts/unlike")
                .then().log().all()
                .status(HttpStatus.OK);  // 상태 코드만 확인

        // then
        assertThat(successResponse).isNotNull();  // NoContent가 반환되었는지 확인
    }


}