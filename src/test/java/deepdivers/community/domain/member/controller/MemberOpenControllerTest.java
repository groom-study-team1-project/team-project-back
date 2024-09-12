package deepdivers.community.domain.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.controller.api.MemberApiController;
import deepdivers.community.domain.member.controller.open.MemberOpenOpenController;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.ImageUploadResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.dto.response.statustype.MemberStatusType;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.token.dto.TokenResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(controllers = {
        MemberOpenOpenController.class,
        MemberApiController.class
})
class MemberOpenControllerTest extends ControllerTest {

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
        NoContent mockResponse = NoContent.from(MemberStatusType.MEMBER_SIGN_UP_SUCCESS);
        given(memberService.signUp(any(MemberSignUpRequest.class))).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc
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
        API<TokenResponse> mockResponse = API.of(MemberStatusType.MEMBER_LOGIN_SUCCESS, tokenResponse);
        given(memberService.login(any(MemberLoginRequest.class))).willReturn(mockResponse);

        MemberLoginRequest loginRequest = new MemberLoginRequest("test@email.com", "test1234!");

        // when
        API<TokenResponse> response = RestAssuredMockMvc
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
        MemberProfileResponse memberProfileResponse = MemberProfileResponse.from(member);
        API<MemberProfileResponse> mockResponse = API.of(MemberStatusType.VIEW_OTHER_PROFILE_SUCCESS, memberProfileResponse);
        given(memberService.getProfile(any(Member.class), anyLong())).willReturn(mockResponse);
        Long profileOwnerId = 1L;

        // when
        API<MemberProfileResponse> response = RestAssuredMockMvc.given().log().all()
                .pathParam("memberId", profileOwnerId)
                .when().get("/api/members/{memberId}/me")
                .then().log().all()
                .status(HttpStatus.OK)
                .extract()
                .as(new TypeRef<>(){});

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
        ImageUploadResponse uploadResponse = ImageUploadResponse.of(imageUrl);
        API<ImageUploadResponse> mockResponse = API.of(MemberStatusType.UPLOAD_IMAGE_SUCCESS, uploadResponse);
        given(memberService.profileImageUpload(any(MultipartFile.class), anyLong())).willReturn(mockResponse);

        // when
        API<ImageUploadResponse> response = RestAssuredMockMvc.given().log().all()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .multiPart("imageFile", contentBody, MediaType.IMAGE_PNG_VALUE)
                .when().post("/api/members/profile-image")
                .then().log().all()
                .status(HttpStatus.OK)
                .extract()
                .as(new TypeRef<>(){});

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

    @Test
    @DisplayName("닉네임 중복검사가 성공적으로 처리되면 200OK를 반환한다.")
    void validateNicknameSuccessfullyReturns200OK() {
        // given
        String nickname = "안녕하세요";
        NoContent mockResponse = NoContent.from(MemberStatusType.NICKNAME_VALIDATE_SUCCESS);
        given(memberService.validateUniqueNickname(anyString())).willReturn(mockResponse);

        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .queryParam("nickname", nickname)
            .when().get("/members/validate/nickname")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>(){});

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("이메일 중복검사가 성공적으로 처리되면 200OK를 반환한다.")
    void validateEmailSuccessfullyReturns200OK() {
        // given
        String email = "email@mail.com";
        NoContent mockResponse = NoContent.from(MemberStatusType.EMAIL_VALIDATE_SUCCESS);
        given(memberService.validateUniqueEmail(anyString())).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .queryParam("email", email)
            .when().get("/members/validate/email")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>(){});

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("닉네임 정보가 없으면 400 Bad Request 를 반환한다.")
    void nullNicknameQueryReturns400BadRequest() {
        // given
        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .when().get("/members/validate/nickname")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(203))
            .body("message", containsString("파라미터가 필요합니다."));
    }

    @Test
    @DisplayName("이메일 정보가 없으면 400 Bad Request 를 반환한다.")
    void nullEmailQueryReturns400BadRequest() {
        // given
        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .when().get("/members/validate/email")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(203))
            .body("message", containsString("파라미터가 필요합니다."));
    }

    @Test
    @DisplayName("올바른 프로필 정보 수정시 200 OK가 떨어진다.")
    void updateProfileReturns200OK() {
        // given
        MemberProfileRequest request = new MemberProfileRequest("test","test","","010-1234-5678","","");
        MemberSignUpRequest signUpRequest = new MemberSignUpRequest("test@email.com", "test1234!", "test", "test", "010-1234-5678");
        Member member = Member.of(signUpRequest, encryptor);
        MemberProfileResponse memberProfileResponse = MemberProfileResponse.from(member);
        API<MemberProfileResponse> mockResponse = API.of(MemberStatusType.UPDATE_PROFILE_SUCCESS, memberProfileResponse);
        given(memberService.updateProfile(1L, request)).willReturn(mockResponse);

        // when
        API<MemberProfileResponse> response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().put("/api/members/profile")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>(){});

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("프로필 정보 수정시 닉네임 정보가 없다면 400 BadRequest 가 떨어진다.")
    void profileUpdateNullNicknameReturns400BadRequest() {
        // given
        MemberProfileRequest request = new MemberProfileRequest("","test","","010-1234-5678","","");

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().put("/api/members/profile")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 닉네임 정보가 필요합니다."));
    }

    @Test
    @DisplayName("프로필 정보 수정시 이미지 정보가 없다면 400 BadRequest 가 떨어진다.")
    void profileUpdateNullImageUrlReturns400BadRequest() {
        // given
        MemberProfileRequest request = new MemberProfileRequest("test","","","010-1234-5678","","");

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().put("/api/members/profile")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 이미지 정보가 필요합니다."));
    }

    @Test
    @DisplayName("프로필 정보 수정시 전화번호 정보가 없다면 400 BadRequest 가 떨어진다.")
    void profileUpdateNullPhoneNumberReturns400BadRequest() {
        // given
        MemberProfileRequest request = new MemberProfileRequest("test","test","","","","");

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().put("/api/members/profile")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 전화번호 정보가 필요합니다."));
    }

}