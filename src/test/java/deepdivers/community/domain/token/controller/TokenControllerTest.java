package deepdivers.community.domain.token.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.token.dto.ReissueResponse;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.domain.token.dto.TokenStatusType;
import deepdivers.community.domain.token.service.TokenService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = TokenController.class)
class TokenControllerTest extends ControllerTest {

    @MockBean
    TokenService tokenService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) throws Exception {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        mockingAuthArgumentResolver();
    }

    @Test
    @DisplayName("회원가입 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    void reIssueSuccessfullyReturns200OK() {
        // given
        String accessToken = "Bearer accessToken";
        String refreshToken = "refreshToken";
        TokenResponse tokenRes = TokenResponse.of("new access", "new refresh");
        StatusResponse statusRes = StatusResponse.from(TokenStatusType.RE_ISSUE_SUCCESS);
        ReissueResponse mockRes = new ReissueResponse(statusRes, tokenRes);
        given(tokenService.reIssueAccessToken(anyString(), anyString())).willReturn(mockRes);

        // when
        ReissueResponse response = RestAssuredMockMvc
                .given().log().all()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header("Refresh-Token", refreshToken)
                .when().patch("/token/re-issue")
                .then().log().all()
                .status(HttpStatus.OK)
                .extract()
                .as(new TypeRef<>(){});

        // then
        assertThat(response).isNotNull();
        assertThat(response.status().code()).isEqualTo(TokenStatusType.RE_ISSUE_SUCCESS.getCode());
        assertThat(response.status().message()).isEqualTo(TokenStatusType.RE_ISSUE_SUCCESS.getMessage());
        assertThat(response.result().accessToken()).isEqualTo("new access");
        assertThat(response.result().refreshToken()).isEqualTo("new refresh");
        assertThat(response).usingRecursiveComparison().isEqualTo(mockRes);
    }

}