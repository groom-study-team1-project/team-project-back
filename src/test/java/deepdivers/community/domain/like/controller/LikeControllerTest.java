package deepdivers.community.domain.like.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.like.dto.LikeRequest;
import deepdivers.community.domain.like.dto.code.LikeStatusCode;
import deepdivers.community.domain.like.service.LikeService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


@WebMvcTest(controllers = LikeController.class)
public class LikeControllerTest extends ControllerTest {

    @MockBean private LikeService likeService;

    @BeforeEach
    void setUp() {
        mockingAuthArgumentResolver();
    }

    @Test
    void 댓글_좋아요_요청이_성공한다() {
        // given
        LikeRequest request = new LikeRequest(1L);
        NoContent mockResponse = NoContent.from(LikeStatusCode.COMMENT_LIKE_SUCCESS);
        given(likeService.likeComment(any(LikeRequest.class), anyLong())).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/likes/comments")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    void 댓글_좋아요시_댓글_식별자가_없으면_예외가_발생한다() {
        // given
        LikeRequest request = new LikeRequest(null);

        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/likes/comments")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("대상 정보가 필요합니다."));
    }

    @Test
    void 댓글_좋아요_취소_요청이_성공한다() {
        // given
        LikeRequest request = new LikeRequest(1L);
        NoContent mockResponse = NoContent.from(LikeStatusCode.COMMENT_UNLIKE_SUCCESS);
        given(likeService.unlikeComment(any(LikeRequest.class), anyLong())).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().delete("/api/likes/comments")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    void 댓글_좋아요_취소시_댓글_식별자가_없으면_예외가_발생한다() {
        // given
        LikeRequest request = new LikeRequest(null);

        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().delete("/api/likes/comments")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("대상 정보가 필요합니다."));
    }

    @Test
    @DisplayName("게시글 좋아요 추가 성공 시 200 OK 반환")
    void likeSuccess() {
        // given
        NoContent mockResponse = NoContent.from(LikeStatusCode.POST_LIKE_SUCCESS);
        given(likeService.likePost(any(LikeRequest.class), anyLong())).willReturn(mockResponse);

        LikeRequest request = new LikeRequest(1L);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .when().post("/api/likes/posts")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("게시글 좋아요 삭제 성공 시 200 OK 반환")
    void unLikeSuccess() {
        // given
        NoContent mockResponse = NoContent.from(LikeStatusCode.POST_UNLIKE_SUCCESS);
        given(likeService.unlikePost(any(LikeRequest.class), anyLong())).willReturn(mockResponse);

        LikeRequest request = new LikeRequest(1L);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .when().delete("/api/likes/posts")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

}
