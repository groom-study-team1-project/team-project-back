package deepdivers.community.domain.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.dto.code.MemberStatusCode;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.member.entity.MemberRole;
import deepdivers.community.domain.member.controller.interfaces.MemberQueryRepository;
import deepdivers.community.domain.token.dto.response.TokenResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = MemberOpenController.class)
class MemberOpenControllerTest extends ControllerTest {

    @MockBean private MemberQueryRepository memberQueryRepository;

    /*
    * 회원가입 컨트롤러 테스트
    * */
    @Test
    @DisplayName("회원가입 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    void signUpSuccessfullyReturns200OK() {
        // given
        MemberSignUpRequest request = new MemberSignUpRequest("test@email.com", "test1234!", "test", "test",
            "010-1234-5678");

        Member account = Member.of(request, this.passwordEncryptor);
        NoContent mockResponse = NoContent.from(MemberStatusCode.MEMBER_SIGN_UP_SUCCESS);
        given(memberService.signUp(any(MemberSignUpRequest.class))).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .when().post("/open/members/sign-up")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("회원가입 요청 시 이메일 정보가 존재하지 않는다면 400 BadRequest 를 반환한다.")
    void signUpNullEmailReturns400BadRequest() {
        // given
        MemberSignUpRequest request = new MemberSignUpRequest(null, "test1234!", "테스트", "테스트", "010-1234-5678");

        // when, then
        RestAssuredMockMvc
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .when().post("/open/members/sign-up")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 이메일 정보가 필요합니다."));
    }

    @Test
    @DisplayName("회원가입 요청 시 닉네임 정보가 존재하지 않는다면 400 BadRequest 를 반환한다.")
    void signUpNullNicknameReturns400BadRequest() {
        // given
        MemberSignUpRequest request = new MemberSignUpRequest("test@email.com", "test1234!", null, "테스트",
            "010-1234-5678");

        // when, then
        RestAssuredMockMvc
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .when().post("/open/members/sign-up")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 닉네임 정보가 필요합니다."));
    }

    @Test
    @DisplayName("회원가입 요청 시 전화번호 정보가 존재하지 않는다면 400 BadRequest 를 반환한다.")
    void signUpNullPhoneNumberReturns400BadRequest() {
        // given
        MemberSignUpRequest request = new MemberSignUpRequest("test@email.com", "test1234!", "테스트", "테스트", null);

        // when, then
        RestAssuredMockMvc
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .when().post("/open/members/sign-up")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 전화번호 정보가 필요합니다."));
    }


    /*
     * 로그인 컨트롤러 테스트
     * */
    @Test
    @DisplayName("로그인 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    void loginSuccessfullyReturns200OK() {
        // given
        TokenResponse tokenResponse = TokenResponse.of("1", "1");
        API<TokenResponse> mockResponse = API.of(MemberStatusCode.MEMBER_LOGIN_SUCCESS, tokenResponse);
        given(memberService.login(any(MemberLoginRequest.class))).willReturn(mockResponse);

        MemberLoginRequest loginRequest = new MemberLoginRequest("test@email.com", "test1234!");

        // when
        API<TokenResponse> response = RestAssuredMockMvc
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .when().post("/open/members/login")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("로그인 시 이메일 정보가 없으면 400 bad request 를 반환한다.")
    void loginNullEmailReturns400BadRequest() {
        // given
        MemberLoginRequest loginRequest = new MemberLoginRequest(null, "test1234!");

        // when, then
        RestAssuredMockMvc
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .when().post("/open/members/login")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 이메일 정보가 필요합니다."));
    }

    @Test
    @DisplayName("로그인 시 비밀번호 정보가 없으면 400 bad request 를 반환한다.")
    void loginNullPasswordReturns400BadRequest() {
        // given
        MemberLoginRequest loginRequest = new MemberLoginRequest("test@email.com", null);

        // when, then
        RestAssuredMockMvc
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .when().post("/open/members/login")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 비밀번호 정보가 필요합니다."));
    }

    @Test
    @DisplayName("멤버 id로 프로필 조회를 할 수 있다.")
    void searchMemberProfileFromMemberId() {
        // given
        MemberProfileResponse mockResponse = new MemberProfileResponse(1L, "", MemberRole.NORMAL, "", "", "", "", "", "", 0, 0, false);
        given(memberQueryRepository.getMemberProfile(anyLong(), anyLong())).willReturn(mockResponse);

        // when
        API<MemberProfileResponse> result = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .pathParam("memberId", 1L)
            .when().get("/open/members/me/{memberId}")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        API<MemberProfileResponse> mockResult = API.of(MemberStatusCode.GET_PROFILE_SUCCESS, mockResponse);
        assertThat(result).usingRecursiveComparison().isEqualTo(mockResult);
    }

}