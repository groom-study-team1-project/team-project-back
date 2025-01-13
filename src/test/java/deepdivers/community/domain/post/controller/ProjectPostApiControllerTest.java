package deepdivers.community.domain.post.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.PostRequestFactory;
import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.dto.code.PostStatusCode;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import deepdivers.community.domain.post.service.ProjectPostService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@WebMvcTest(ProjectPostApiController.class)
class ProjectPostApiControllerTest extends ControllerTest {

    @MockBean ProjectPostService projectPostService;

    @BeforeEach
    void setUp() {
        mockingAuthArgumentResolver();
    }

    @Test
    void 프로젝트_게시글_생성_요청이_성공적으로_응답한다() {
        // given
        ProjectPostRequest request = PostRequestFactory.createProjectPostRequest();
        API<Long> mockResponse = API.of(PostStatusCode.PROJECT_POST_CREATE_SUCCESS, 1L);
        given(projectPostService.createProjectPost(any(Member.class), any(ProjectPostRequest.class)))
            .willReturn(mockResponse);

        // when
        API<Long> response = RestAssuredMockMvc.given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .when().post("/api/projects/upload")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    void ProjectPostRequest_Dto에_슬라이드_이미지_정보가_없으면_예외가_발생한다() {
        // given
        ProjectPostRequest request = new ProjectPostRequest("", "", "", 1L, List.of(), List.of(), null);

        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .when().post("/api/projects/upload")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101));
    }

    @Test
    void 프로젝트_게시글_수정_요청이_성공적으로_응답한다() {
        // given
        ProjectPostRequest request = PostRequestFactory.createProjectPostRequest();
        API<Long> mockResponse = API.of(PostStatusCode.PROJECT_POST_UPDATE_SUCCESS, 1L);
        given(projectPostService.updateProjectPost(anyLong(), any(Member.class), any(ProjectPostRequest.class)))
            .willReturn(mockResponse);

        // when
        API<Long> response = RestAssuredMockMvc.given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .pathParam("projectId", 1L)
            .when().post("/api/projects/edit/{projectId}")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    void 프로젝트_게시글_삭제_요청이_성공적으로_응답한다() {
        // given
        ProjectPostRequest request = PostRequestFactory.createProjectPostRequest();
        NoContent mockResponse = NoContent.from(PostStatusCode.PROJECT_POST_DELETE_SUCCESS);
        given(projectPostService.deletePost(anyLong(), any(Member.class)))
            .willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .pathParam("projectId", 1L)
            .when().delete("/api/projects/remove/{projectId}")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

}