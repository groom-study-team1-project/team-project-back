package deepdivers.community.domain.post.controller.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.EditCommentRequest;
import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.dto.request.RemoveCommentRequest;
import deepdivers.community.domain.post.dto.request.WriteCommentRequest;
import deepdivers.community.domain.post.dto.request.WriteReplyRequest;
import deepdivers.community.domain.post.dto.response.statustype.CommentStatusType;
import deepdivers.community.domain.post.service.CommentService;
import deepdivers.community.domain.post.service.LikeService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

@WebMvcTest(controllers = CommentApiController.class)
class CommentApiControllerTest extends ControllerTest {

    @MockBean LikeService likeService;
    @MockBean CommentService commentService;

    @BeforeEach
    void init() {
        mockingAuthArgumentResolver();
    }

    @Test
    void 댓글_작성_요청이_성공한다() {
        // given
        WriteCommentRequest request = new WriteCommentRequest(1L, "content");
        NoContent mockResponse = NoContent.from(CommentStatusType.COMMENT_CREATE_SUCCESS);
        given(commentService.writeComment(any(Member.class), any(WriteCommentRequest.class)))
            .willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/comments/write")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    void 댓글_작성시_게시글_식별자가_없으면_예외가_발생한다() {
        // given
        WriteCommentRequest request = new WriteCommentRequest(null, "content");

        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/comments/write")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("게시글 정보가 필요합니다."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 댓글_작성시_댓글_내용이_없으면_예외가_발생한다(String content) {
        // given
        WriteCommentRequest request = new WriteCommentRequest(1L, content);

        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/comments/write")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("댓글 내용이 필요합니다."));
    }

    @Test
    void 답글_작성_요청이_성공한다() {
        // given
        WriteReplyRequest request = new WriteReplyRequest(1L, "content");
        NoContent mockResponse = NoContent.from(CommentStatusType.COMMENT_CREATE_SUCCESS);
        given(commentService.writeReply(any(Member.class), any(WriteReplyRequest.class)))
            .willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/comments/write/reply")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    void 답글_작성시_댓글_식별자가_없으면_예외가_발생한다() {
        // given
        WriteReplyRequest request = new WriteReplyRequest(null, "content");

        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/comments/write/reply")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("댓글 정보가 필요합니다."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 답글_작성시_댓글_내용이_없으면_예외가_발생한다(String content) {
        // given
        WriteReplyRequest request = new WriteReplyRequest(1L, content);

        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/comments/write/reply")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("댓글 내용이 필요합니다."));
    }

    @Test
    void 댓글_삭제_요청이_성공한다() {
        // given
        RemoveCommentRequest request = new RemoveCommentRequest(1L);
        NoContent mockResponse = NoContent.from(CommentStatusType.COMMENT_CREATE_SUCCESS);
        given(commentService.removeComment(any(Member.class), any(RemoveCommentRequest.class)))
            .willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().delete("/api/comments/remove")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    void 댓글_삭제시_댓글_식별자가_없으면_예외가_발생한다() {
        // given
        RemoveCommentRequest request = new RemoveCommentRequest(null);

        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().delete("/api/comments/remove")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("댓글 정보가 필요합니다."));
    }

    @Test
    void 댓글_수정_요청이_성공한다() {
        // given
        EditCommentRequest request = new EditCommentRequest(1L, "content");
        NoContent mockResponse = NoContent.from(CommentStatusType.COMMENT_CREATE_SUCCESS);
        given(commentService.updateComment(any(Member.class), any(EditCommentRequest.class)))
            .willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/comments/edit")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    void 댓글_수정시_댓글_식별자가_없으면_예외가_발생한다() {
        // given
        EditCommentRequest request = new EditCommentRequest(null, "content");

        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/comments/edit")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("댓글 정보가 필요합니다."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 댓글_수정시_댓글_내용이_없으면_예외가_발생한다(String content) {
        // given
        EditCommentRequest request = new EditCommentRequest(1L, content);

        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/comments/edit")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("댓글 내용이 필요합니다."));
    }

    @Test
    void 댓글_좋아요_요청이_성공한다() {
        // given
        LikeRequest request = new LikeRequest(1L);
        NoContent mockResponse = NoContent.from(CommentStatusType.COMMENT_CREATE_SUCCESS);
        given(likeService.likeComment(any(LikeRequest.class), anyLong())).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/comments/like")
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
            .when().post("/api/comments/like")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("대상 정보가 필요합니다."));
    }

    @Test
    void 댓글_좋아요_취소_요청이_성공한다() {
        // given
        LikeRequest request = new LikeRequest(1L);
        NoContent mockResponse = NoContent.from(CommentStatusType.COMMENT_CREATE_SUCCESS);
        given(likeService.unlikeComment(any(LikeRequest.class), anyLong())).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/api/comments/unlike")
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
            .when().post("/api/comments/unlike")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("대상 정보가 필요합니다."));
    }

}