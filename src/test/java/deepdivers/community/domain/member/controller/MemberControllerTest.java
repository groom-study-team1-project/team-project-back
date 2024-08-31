package deepdivers.community.domain.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.member.controller.api.MemberApiController;
import deepdivers.community.domain.member.controller.open.MemberController;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberLoginResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileImageResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
import deepdivers.community.domain.member.dto.response.result.type.MemberStatusType;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.token.dto.TokenResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(controllers = {
        MemberController.class,
        MemberApiController.class
})
class MemberControllerTest extends ControllerTest {

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) throws Exception {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        mockingAuthArgumentResolver();
    }

    @Test
    @DisplayName("회원가입 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    void signUpSuccessfullyReturns200OK() {
        // given
        MemberSignUpRequest request = new MemberSignUpRequest("test@email.com", "test1234!", "test", "test", "010-1234-5678");

        Member account = Member.of(request, this.encryptor);
        MemberSignUpResponse mockResponse = MemberSignUpResponse.of(MemberStatusType.MEMBER_SIGN_UP_SUCCESS, account);
        given(memberService.signUp(any(MemberSignUpRequest.class))).willReturn(mockResponse);

        // when
        MemberSignUpResponse response = RestAssuredMockMvc
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when().post("/members/sign-up")
                .then().log().all()
                .status(HttpStatus.OK)
                .extract()
                .as(new TypeRef<>(){});

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("회원가입 요청 시 이메일 형식이 올바르지 않다면 400 BadRequest 를 반환한다.")
    void signUpWrongEmailFormatReturns400BadRequest() {
        // given
        MemberSignUpRequest request = new MemberSignUpRequest("test@ma .com", "test1234!", "테스트", "테스트", "010-1234-5678");

        // when, then
        RestAssuredMockMvc
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when().post("/members/sign-up")
                .then().log().all()
                .status(HttpStatus.BAD_REQUEST)
                .body("code", equalTo(101))
                .body("message", containsString("이메일 형식으로 입력해주세요."));
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
                .when().post("/members/sign-up")
                .then().log().all()
                .status(HttpStatus.BAD_REQUEST)
                .body("code", equalTo(101))
                .body("message", containsString("사용자 이메일 정보가 필요합니다."));
    }

    @Test
    @DisplayName("회원가입 요청 시 닉네임 정보가 존재하지 않는다면 400 BadRequest 를 반환한다.")
    void signUpNullNicknameReturns400BadRequest() {
        // given
        MemberSignUpRequest request = new MemberSignUpRequest("test@email.com", "test1234!", null, "테스트", "010-1234-5678");

        // when, then
        RestAssuredMockMvc
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when().post("/members/sign-up")
                .then().log().all()
                .status(HttpStatus.BAD_REQUEST)
                .body("code", equalTo(101))
                .body("message", containsString("사용자 닉네임 정보가 필요합니다."));
    }

    @Test
    @DisplayName("회원가입 요청 시 이미지 주소 정보가 존재하지 않는다면 400 BadRequest 를 반환한다.")
    void signUpNullImageUrlReturns400BadRequest() {
        // given
        MemberSignUpRequest request = new MemberSignUpRequest("test@email.com", "test1234!", "테스트", null, "010-1234-5678");

        // when, then
        RestAssuredMockMvc
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when().post("/members/sign-up")
                .then().log().all()
                .status(HttpStatus.BAD_REQUEST)
                .body("code", equalTo(101))
                .body("message", containsString("사용자 이미지 정보가 필요합니다."));
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
                .when().post("/members/sign-up")
                .then().log().all()
                .status(HttpStatus.BAD_REQUEST)
                .body("code", equalTo(101))
                .body("message", containsString("사용자 전화번호 정보가 필요합니다."));
    }

    @Test
    @DisplayName("로그인 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    void loginSuccessfullyReturns200OK() {
        // given
        TokenResponse tokenResponse = TokenResponse.of("1", "1");
        MemberLoginResponse mockResponse = MemberLoginResponse.of(MemberStatusType.MEMBER_LOGIN_SUCCESS, tokenResponse);
        given(memberService.login(any(MemberLoginRequest.class))).willReturn(mockResponse);

        MemberLoginRequest loginRequest = new MemberLoginRequest("test@email.com", "test1234!");

        // when
        MemberLoginResponse response = RestAssuredMockMvc
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginRequest)
                .when().post("/members/login")
                .then().log().all()
                .status(HttpStatus.OK)
                .extract()
                .as(new TypeRef<>(){});

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("프로필 조회 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    void findProfileSuccessfullyReturns200OK() {
        // given
        MemberSignUpRequest signUpRequest = new MemberSignUpRequest("test@email.com", "test1234!", "test", "test", "010-1234-5678");
        Member member = Member.of(signUpRequest, encryptor);
        MemberProfileResponse mockResponse = MemberProfileResponse.of(
                MemberStatusType.VIEW_OTHER_PROFILE_SUCCESS, member);
        given(memberService.getProfile(any(Member.class), anyLong())).willReturn(mockResponse);
        Long profileOwnerId = 1L;

        // when
        MemberProfileResponse response = RestAssuredMockMvc.given().log().all()
                .pathParam("memberId", profileOwnerId)
                .when().get("/api/members/{memberId}/me")
                .then().log().all()
                .status(HttpStatus.OK)
                .extract()
                .as(MemberProfileResponse.class);

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("프로필 이미지 업로드가 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    void uploadProfileImageSuccessfullyReturns200OK() {
        // given
        String contentBody = "image";
        String imageUrl = "testurl.png";
        MemberProfileImageResponse mockResponse = MemberProfileImageResponse.of(MemberStatusType.UPLOAD_IMAGE_SUCCESS, imageUrl);
        given(memberService.profileImageUpload(any(MultipartFile.class), anyLong())).willReturn(mockResponse);

        // when
        MemberProfileImageResponse response = RestAssuredMockMvc.given().log().all()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .multiPart("imageFile", contentBody, MediaType.IMAGE_PNG_VALUE)
                .when().post("/api/members/profile-image")
                .then().log().all()
                .status(HttpStatus.OK)
                .extract()
                .as(MemberProfileImageResponse.class);

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("이미지 파일이 업로드 되지 않으면 서버 에러가 발생해야한다.")
    void notAttachImageFileReturnInternalServerError() {
        // given

        // when, then
        RestAssuredMockMvc.given().log().all()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .when().post("/api/members/profile-image")
                .then().log().all()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("code", equalTo(100))
                .body("message", containsString("알 수 없는 서버 에러가 발생했습니다."));
    }


}