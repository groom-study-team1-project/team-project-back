package deepdivers.community.domain.image.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import deepdivers.community.domain.ControllerTest;
import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.image.application.dto.request.GetPresignRequest;
import deepdivers.community.domain.image.application.dto.response.GetPresignResponse;
import deepdivers.community.domain.image.application.dto.response.statustype.UploaderStatusCode;
import deepdivers.community.infra.aws.s3.KeyType;
import deepdivers.community.infra.aws.s3.S3PresignManager;
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

@WebMvcTest(controllers = UploaderOpenController.class)
class UploaderOpenControllerTest extends ControllerTest {

    @MockBean private S3PresignManager s3PresignManager;

    @Test
    @DisplayName("서명 된 URL 생성이 성공하면 200OK를 반환한다.")
    void givenMockingWhenRequestThenReturnResponseEqualToMockResponse() {
        // given
        GetPresignResponse presignResponse = new GetPresignResponse("key", "presign", "access");
        API<GetPresignResponse> mockResponse = API.of(UploaderStatusCode.GENERATE_PRESIGN_SUCCESS, presignResponse);
        given(s3PresignManager.generateKey(anyString(), any(KeyType.class))).willReturn("key");
        given(s3PresignManager.generatePreSignedUrl(anyString(), anyString())).willReturn("presign");
        given(s3PresignManager.generateAccessUrl(anyString())).willReturn("access");

        // when
        API<GetPresignResponse> response = RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(new GetPresignRequest("contentType", KeyType.POST))
            .when().post("/open/uploader/presigned-url-generation")
            .then().log().all()
            .status(HttpStatus.OK)
            .extract()
            .as(new TypeRef<>() {
            });

        // then
        assertThat(response).isNotNull();
        assertThat(response).usingRecursiveComparison().isEqualTo(mockResponse);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("contentType 정보가 없다면 400 BadRequest를 반환한다.")
    void givenNullOrEmptyContentTypeWhenRequestThenThrowException(String contentType) {
        // given
        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(new GetPresignRequest(contentType, KeyType.POST))
            .when().post("/open/uploader/presigned-url-generation")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("업로드 할 파일의 contentType은 필수입니다."));
    }

    @Test
    @DisplayName("contentType 정보가 없다면 400 BadRequest를 반환한다.")
    void givenNullKeyTypeWhenRequestThenThrowException() {
        // given
        // when & then
        RestAssuredMockMvc.given().log().all()
            .contentType(ContentType.JSON)
            .body(new GetPresignRequest("contentType", null))
            .when().post("/open/uploader/presigned-url-generation")
            .then().log().all()
            .status(HttpStatus.BAD_REQUEST)
            .body("code", equalTo(101))
            .body("message", containsString("업로드 할 파일의 KeyType은 필수입니다."));
    }

}