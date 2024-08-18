package deepdivers.community.domain.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.request.info.MemberAccountInfo;
import deepdivers.community.domain.member.dto.request.info.MemberRegisterInfo;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
import deepdivers.community.domain.member.dto.response.result.type.MemberResultType;
import deepdivers.community.domain.member.model.Account;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.global.config.EncryptorConfig;
import deepdivers.community.global.exception.GlobalExceptionHandler;
import deepdivers.community.utility.encryptor.Encryptor;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(controllers = MemberController.class)
@Import(EncryptorConfig.class)
class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private Encryptor encryptor;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.standaloneSetup(
                MockMvcBuilders
                        .standaloneSetup(new MemberController(memberService))
                        .setControllerAdvice(GlobalExceptionHandler.class)
        );
    }

    @Test
    @DisplayName("회원가입 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    void signUpSuccessfullyReturns200OK() {
        // given
        MemberAccountInfo accountInfo = new MemberAccountInfo("test@email.com", "test1234!");
        MemberRegisterInfo registerInfo = new MemberRegisterInfo("테스트", "테스트", "010-1234-5678");
        MemberSignUpRequest request = new MemberSignUpRequest(accountInfo, registerInfo);

        Account account = Account.accountSignUp(accountInfo, this.encryptor, Member.registerMember(registerInfo));
        MemberSignUpResponse mockResponse = MemberSignUpResponse.of(MemberResultType.MEMBER_SIGN_UP_SUCCESS, account);
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
                .as(MemberSignUpResponse.class);

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    MemberSignUpRequest getMockRequest(String p1, String p2, String p3, String p4, String p5) {
        MemberAccountInfo accountInfo = new MemberAccountInfo(p1, p2);
        MemberRegisterInfo registerInfo = new MemberRegisterInfo(p3, p4, p5);
        return new MemberSignUpRequest(accountInfo, registerInfo);
    }

    @Test
    @DisplayName("회원가입 요청 시 이메일 형식이 올바르지 않다면 400 BadRequest 를 반환한다.")
    void signUpWrongEmailFormatReturns400BadRequest() {
        // given
        MemberSignUpRequest request = getMockRequest("test@ma .com", "test1234!", "테스트", "테스트", "010-1234-5678");
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
        MemberSignUpRequest request = getMockRequest(null, "test1234!", "테스트", "테스트", "010-1234-5678");

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
        MemberSignUpRequest request = getMockRequest("test@email.com", "test1234!", null, "테스트", "010-1234-5678");

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
        MemberSignUpRequest request = getMockRequest("test@email.com", "test1234!", "테스트", null, "010-1234-5678");


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
        MemberSignUpRequest request = getMockRequest("test@email.com", "test1234!", "테스트", "테스트", null);

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


}