package deepdivers.community.domain.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.controller.open.AccountOpenController;
import deepdivers.community.domain.member.dto.request.AuthenticateEmailRequest;
import deepdivers.community.domain.member.dto.request.ResetPasswordRequest;
import deepdivers.community.domain.member.dto.request.VerifyEmailRequest;
import deepdivers.community.domain.member.dto.response.statustype.AccountStatusType;
import deepdivers.community.domain.member.service.AccountService;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

@WebMvcTest(controllers = AccountOpenController.class)
class AccountOpenControllerTest extends ControllerTest {

    @MockBean
    private AccountService accountService;

    /*
    * 닉네임 중복 검사 컨트롤러 테스트
    * */
    @Test
    @DisplayName("닉네임 중복검사가 성공적으로 처리되면 200OK를 반환한다.")
    void validateNicknameSuccessfullyReturns200OK() {
        // given
        String nickname = "안녕하세요";
        NoContent mockResponse = NoContent.from(AccountStatusType.NICKNAME_VALIDATE_SUCCESS);
        given(accountService.verifyNickname(anyString())).willReturn(mockResponse);

        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .queryParam("nickname", nickname)
            .when().get("/open/accounts/verify/nickname")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("닉네임 정보가 없으면 400 Bad Request 를 반환한다.")
    void nullNicknameQueryReturns400BadRequest() {
        // given
        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .when().get("/open/accounts/verify/nickname")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(203))
            .body("message", containsString("파라미터가 필요합니다."));
    }

    /*
    * 이메일 인증코드 전송 컨트롤러 테스트
    * */
    @Test
    @DisplayName("이메일 인증코드 요청 시 성공적으로 처리되면 200OK를 반환한다.")
    void authenticateEmailSuccessfullyReturns200OK() {
        // given
        AuthenticateEmailRequest request = new AuthenticateEmailRequest("email@test.com");
        NoContent mockResponse = NoContent.from(AccountStatusType.SEND_VERIFY_CODE_SUCCESS);
        given(accountService.emailAuthentication(request)).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/authenticate/email")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이메일 인증코드 요청시 이메일 정보가 없으면 400 bad reuqest를 반환한다.")
    void authenticateNullEmailReturns400BadRequest(String email) {
        // given
        AuthenticateEmailRequest request = new AuthenticateEmailRequest(email);

        // when then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/authenticate/email")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 이메일 정보가 필요합니다."));
    }

    @Test
    @DisplayName("이메일 인증코드 요청 시 이메일 형식이 아니면 400 bad request 를 반환한다.")
    void authenticateNoEmailFormatReturns400BadRequest() {
        // given
        AuthenticateEmailRequest request = new AuthenticateEmailRequest("notemail");

        // when then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/authenticate/email")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("이메일 형식이 아닙니다."));
    }

    /*
    * 이메일 인증코드 검사 컨트롤러 테스트
    * */
    @Test
    @DisplayName("이메일 인증코드 요청 시 성공적으로 처리되면 200OK를 반환한다.")
    void verifyEmailSuccessfullyReturns200OK() {
        // given
        VerifyEmailRequest request = new VerifyEmailRequest("email@test.com", "111111");
        NoContent mockResponse = NoContent.from(AccountStatusType.VERIFY_EMAIL_SUCCESS);
        given(accountService.verifyEmail(request)).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/verify/email")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이메일 인증코드 검사 요청 시 이메일 정보가 없으면 400 bad request를 반환한다.")
    void verifyNullEmailShouldBeReturns400BadRequest(String email) {
        // given
        VerifyEmailRequest request = new VerifyEmailRequest(email, "111111");

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/verify/email")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 이메일 정보가 필요합니다."));
    }

    @Test
    @DisplayName("이메일 인증코드 검사 요청 시 이메일 형식이 아니면 400 bad request를 반환한다.")
    void verifyNotEmailFormatShouldBeReturns400BadRequest() {
        // given
        VerifyEmailRequest request = new VerifyEmailRequest("notEmail", "111111");

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/verify/email")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("이메일 형식이 아닙니다."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이메일 인증코드 검사 요청 시 인증코드 정보가 없으면 400 bad request를 반환한다.")
    void verifyNullCodeFormatShouldBeReturns400BadRequest(String code) {
        // given
        VerifyEmailRequest request = new VerifyEmailRequest("email@test.com", code);

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/verify/email")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("인증 코드 정보가 필요합니다"));
    }

    @Test
    void 비밀번호_찾기_인증_요청이_성공한다() {
        // given
        AuthenticateEmailRequest request = new AuthenticateEmailRequest("email@test.com");
        NoContent mockResponse = NoContent.from(AccountStatusType.SEND_VERIFY_CODE_SUCCESS);
        given(accountService.passwordAuthentication(request)).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/authenticate/password")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 비밀번호_인증_요청시_email_정보가_없다면_예외가_발생한다(String email) {
        // given
        AuthenticateEmailRequest request = new AuthenticateEmailRequest(email);

        // when then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/authenticate/password")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 이메일 정보가 필요합니다."));
    }

    @Test
    void 비밀번호_찾기_시_이메일_형식이_아니면_예외가_발생한다() {
        // given
        AuthenticateEmailRequest request = new AuthenticateEmailRequest("notemail");

        // when then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/authenticate/password")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("이메일 형식이 아닙니다."));
    }

    @Test
    void 비밀번호_재설정이_성공한다() {
        // given
        ResetPasswordRequest request = new ResetPasswordRequest("email@test.com", "111111");
        NoContent mockResponse = NoContent.from(AccountStatusType.VERIFY_EMAIL_SUCCESS);
        given(accountService.resetPassword(request)).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/reset/password")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 비밀번호_재설정시_이메일_정보가_없으면_예외가_발생한다(String email) {
        // given
        ResetPasswordRequest request = new ResetPasswordRequest(email, "111111");

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/reset/password")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 이메일 정보가 필요합니다."));
    }

    @Test
    @DisplayName("이메일 인증코드 검사 요청 시 이메일 형식이 아니면 400 bad request를 반환한다.")
    void 비밀번호_재설정시_이메일_정보가_이메일_형식이_아니라면_예외가_발생한다() {
        // given
        VerifyEmailRequest request = new VerifyEmailRequest("notEmail", "111111");

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/reset/password")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("이메일 형식이 아닙니다."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 비밀번호_재설정시_비밀번호_정보가_없으면_예외가_발생한다(String password) {
        // given
        ResetPasswordRequest request = new ResetPasswordRequest("email@test.com", password);

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/open/accounts/reset/password")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 비밀번호 정보가 필요합니다."));
    }


    }