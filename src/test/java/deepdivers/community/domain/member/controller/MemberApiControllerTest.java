package deepdivers.community.domain.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.controller.api.MemberApiController;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.request.UpdatePasswordRequest;
import deepdivers.community.domain.member.dto.response.ImageUploadResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.dto.response.statustype.MemberStatusType;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.repository.PostQueryRepository;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(controllers = MemberApiController.class)
class MemberApiControllerTest extends ControllerTest {

    @MockBean
    PostQueryRepository PostQueryRepository;
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) throws Exception {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        mockingAuthArgumentResolver();
    }

    /*
    * 프로필 조회 컨트롤러 테스트
    * */
    @Test
    @DisplayName("프로필 조회 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    void findProfileSuccessfullyReturns200OK() {
        // given
        MemberSignUpRequest signUpRequest = new MemberSignUpRequest("test@email.com", "test1234!", "test", "test",
            "010-1234-5678");
        Member member = Member.of(signUpRequest, encryptor);
        MemberProfileResponse memberProfileResponse = MemberProfileResponse.from(member);
        API<MemberProfileResponse> mockResponse = API.of(MemberStatusType.VIEW_OTHER_PROFILE_SUCCESS,
            memberProfileResponse);
        given(memberService.getProfile(any(Member.class), anyLong())).willReturn(mockResponse);
        Long profileOwnerId = 1L;

        // when
        API<MemberProfileResponse> response = RestAssuredMockMvc.given().log().all()
            .pathParam("memberId", profileOwnerId)
            .when().get("/api/members/me/{memberId}")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    /*
    * 프로필 이미지 업로드 컨트롤러 테스트
    * */
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
            .when().post("/api/members/me/profile-image")
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

    /*
    * 프로필 수정 컨트롤러 테스트
    * */
    @Test
    @DisplayName("올바른 프로필 정보 수정시 200 OK가 떨어진다.")
    void updateProfileReturns200OK() {
        // given
        MemberProfileRequest request = new MemberProfileRequest("test", "test", "", "010-1234-5678", "", "");
        Member member = memberService.getMemberWithThrow(1L);
        API<MemberProfileResponse> mockResponse = API.of(MemberStatusType.UPDATE_PROFILE_SUCCESS,
            MemberProfileResponse.from(member));
        given(memberService.updateProfile(member, request)).willReturn(mockResponse);

        // when
        API<MemberProfileResponse> response = RestAssuredMockMvc.given().log().all()
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
        MemberProfileRequest request = new MemberProfileRequest("", "test", "", "010-1234-5678", "", "");

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
    @DisplayName("프로필 정보 수정시 이미지 정보가 없다면 400 BadRequest 가 떨어진다.")
    void profileUpdateNullImageUrlReturns400BadRequest() {
        // given
        MemberProfileRequest request = new MemberProfileRequest("test", "", "", "010-1234-5678", "", "");

        // when, then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(request)
            .when().put("/api/members/me")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("사용자 이미지 정보가 필요합니다."));
    }

    @Test
    @DisplayName("프로필 정보 수정시 전화번호 정보가 없다면 400 BadRequest 가 떨어진다.")
    void profileUpdateNullPhoneNumberReturns400BadRequest() {
        // given
        MemberProfileRequest request = new MemberProfileRequest("test", "test", "", "", "", "");

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
        NoContent mockResponse = NoContent.from(MemberStatusType.UPDATE_PASSWORD_SUCCESS);
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
