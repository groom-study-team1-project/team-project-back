package deepdivers.community.domain.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.UpdatePasswordRequest;
import deepdivers.community.domain.member.dto.code.MemberStatusCode;
import deepdivers.community.domain.member.entity.Member;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;

@WebMvcTest(controllers = MemberApiController.class)
class MemberApiControllerTest extends ControllerTest {

    @BeforeEach
    void init() {
        mockingAuthArgumentResolver();
    }

    /*
     * 프로필 수정 컨트롤러 테스트
     * */
    @Test
    @DisplayName("올바른 프로필 정보 수정시 200 OK가 떨어진다.")
    void updateProfileReturns200OK() {
        // given
        MemberProfileRequest request = new MemberProfileRequest("test", "test", "", "010-1234-5678", "", "", "EMPTY");
        Member member = memberService.getMemberWithThrow(1L);
        NoContent mockResponse = NoContent.from(MemberStatusCode.UPDATE_PROFILE_SUCCESS);
        given(memberService.updateProfile(member, request)).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().put("/api/members/me")
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
    @DisplayName("프로필 정보 수정시 닉네임 정보가 없다면 400 BadRequest 가 떨어진다.")
    void profileUpdateNullNicknameReturns400BadRequest() {
        // given
        MemberProfileRequest request = new MemberProfileRequest("", "test", "", "010-1234-5678", "", "", "EMPTY");

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().put("/api/members/me")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 닉네임 정보가 필요합니다."));
    }

    @Test
    @DisplayName("프로필 정보 수정시 이미지 정보가 없어도 정상 응답한다.")
    void profileUpdateNullImageUrlReturns200OK() {
        // given
        MemberProfileRequest request = new MemberProfileRequest("test", null, "", "010-1234-5678", "", "", "EMPTY");
        Member member = memberService.getMemberWithThrow(1L);
        NoContent mockResponse = NoContent.from(MemberStatusCode.UPDATE_PROFILE_SUCCESS);
        given(memberService.updateProfile(member, request)).willReturn(mockResponse);

        // when, then
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().put("/api/members/me")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("프로필 정보 수정시 전화번호 정보가 없다면 400 BadRequest 가 떨어진다.")
    void profileUpdateNullPhoneNumberReturns400BadRequest() {
        // given
        MemberProfileRequest request = new MemberProfileRequest("test", "test", "", "", "", "", "EMPTY");

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().put("/api/members/me")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 전화번호 정보가 필요합니다."));
    }

    /*
     * 비밀번호 수정 컨트롤러 테스트
     * */
    @Test
    @DisplayName("올바른 프로필 정보 수정시 200 OK가 떨어진다.")
    void changePasswordReturns200OK() {
        // given
        UpdatePasswordRequest request = new UpdatePasswordRequest("test", "test");
        Member member = memberService.getMemberWithThrow(1L);
        NoContent mockResponse = NoContent.from(MemberStatusCode.UPDATE_PASSWORD_SUCCESS);
        given(memberService.changePassword(member, request)).willReturn(mockResponse);

        // when
        NoContent response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().patch("/api/members/me/password")
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
    @DisplayName("비밀번호 수정시 현재 비밀번호 정보가 없다면 400 BadRequest 가 떨어진다.")
    void passwordUpdateNullCurrentPasswordReturns400BadRequest() {
        // given
        UpdatePasswordRequest request = new UpdatePasswordRequest(null, "test");

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().patch("/api/members/me/password")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("현재 비밀번호 정보가 필요합니다."));
    }

    @Test
    @DisplayName("비밀번호 수정시 새로운 비밀번호 정보가 없다면 400 BadRequest 가 떨어진다.")
    void passwordUpdateNullNewPasswordReturns400BadRequest() {
        // given
        UpdatePasswordRequest request = new UpdatePasswordRequest("test", null);

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().patch("/api/members/me/password")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("새로운 비밀번호 정보가 필요합니다."));
    }

}
