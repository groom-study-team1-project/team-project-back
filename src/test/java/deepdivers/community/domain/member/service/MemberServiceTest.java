package deepdivers.community.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.common.StatusType;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.ImageUploadResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.dto.response.ProfileImageUploadResponse;
import deepdivers.community.domain.member.dto.response.statustype.MemberStatusType;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.model.vo.MemberRole;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
import deepdivers.community.global.security.jwt.AuthHelper;
import deepdivers.community.global.security.jwt.AuthPayload;
import deepdivers.community.utility.uploader.S3Exception;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private AuthHelper authHelper;

    @Test
    @DisplayName("회원 가입이 성공했을 경우를 테스트한다.")
    void signUpSuccessTest() {
        // Given, test.sql
        MemberSignUpRequest request = new MemberSignUpRequest("test@mail.com", "password1234!", "test", "test", "010-1234-5678");
        long lastAccountId = 10L;
        LocalDateTime testStartTime = LocalDateTime.now();

        // When
        MemberSignUpResponse response = memberService.signUp(request);

        // Then
        LocalDateTime testEndTime = LocalDateTime.now();
        StatusType statusType = MemberStatusType.MEMBER_SIGN_UP_SUCCESS;
        MemberSignUpResponse responseResult = response.result();
        assertThat(response).isNotNull();
        assertThat(response.status().code()).isEqualTo(statusType.getCode());
        assertThat(response.status().message()).isEqualTo(statusType.getMessage());
        assertThat(responseResult.id()).isGreaterThan(lastAccountId);
        assertThat(responseResult.nickname()).isEqualTo(request.nickname());
        assertThat(responseResult.createdAt()).isBetween(testStartTime, testEndTime);
    }

    @Test
    @DisplayName("중복 이메일로 회원 가입 시 예외 발생하는 경우를 테스트한다.")
    void signUpDuplicateEmailTest() {
        // Given test.sql
        MemberSignUpRequest request = new MemberSignUpRequest("email1@test.com", "password1!", "test", "test", "010-1234-5678");

        // When & Then
        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.ALREADY_REGISTERED_EMAIL);
    }

    @Test
    @DisplayName("중복 닉네임으로 회원 가입 시 예외가 발생하는지 테스트한다.")
    void signUpDuplicateNicknameTest() {
        // Given
        MemberSignUpRequest request = new MemberSignUpRequest("test@mail.com", "password123!", "User9", "test", "010-1234-5678");

        // When & Then
        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.ALREADY_REGISTERED_NICKNAME);
    }


    @Test
    @DisplayName("로그인이 성공했을 경우를 테스트한다.")
    void loginSuccessTest() {
        // Given, test.sql
        MemberLoginRequest loginRequest = new MemberLoginRequest("email2@test.com", "password2!");

        // When
        MemberLoginResponse response = memberService.login(loginRequest);

        // Then, test.sql
        StatusType statusType = MemberStatusType.MEMBER_LOGIN_SUCCESS;
        TokenResponse responseResult = response.result();

        assertThat(response).isNotNull();
        assertThat(response.status().code()).isEqualTo(statusType.getCode());
        assertThat(response.status().message()).isEqualTo(statusType.getMessage());

        assertThat(responseResult.accessToken()).isNotNull().isNotEmpty();
        assertThat(responseResult.accessToken()).matches("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$");

        assertThat(responseResult.refreshToken()).isNotNull().isNotEmpty();
        assertThat(responseResult.refreshToken()).matches("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$");

        AuthPayload authPayload = authHelper.parseToken(responseResult.accessToken());
        assertThat(authPayload.memberId()).isEqualTo(2L);
        assertThat(authPayload.memberNickname()).isEqualTo("User2");
        assertThat(authPayload.memberRole()).isEqualTo(MemberRole.NORMAL.toString());
    }

    @Test
    @DisplayName("가입되지 않은 계정인 경우를 테스트한다.")
    void loginNotFoundEmailTest() {
        // Given, test.sql
        MemberLoginRequest loginRequest = new MemberLoginRequest("email11@test.com", "password2!");

        // When
        assertThatThrownBy(() -> memberService.login(loginRequest))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.NOT_FOUND_ACCOUNT);
    }

    @Test
    @DisplayName("비밀번호가 틀린 경우를 테스트한다.")
    void loginInvalidPasswordTest() {
        // Given, test.sql
        MemberLoginRequest loginRequest = new MemberLoginRequest("email2@test.com", "password22!");

        // When
        assertThatThrownBy(() -> memberService.login(loginRequest))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.NOT_FOUND_ACCOUNT);
    }

    @Test
    @DisplayName("휴면 계정인 경우를 테스트한다.")
    void loginDormancyAccountTest() {
        // Given, test.sql
        MemberLoginRequest loginRequest = new MemberLoginRequest("email9@test.com", "password9!");

        // When
        assertThatThrownBy(() -> memberService.login(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.MEMBER_LOGIN_DORMANCY);
    }

    @Test
    @DisplayName("탈퇴처리 중인 계정인 경우를 테스트한다.")
    void loginUnRegisterAccountTest() {
        // Given, test.sql
        MemberLoginRequest loginRequest = new MemberLoginRequest("email8@test.com", "password8!");

        // When, Then
        assertThatThrownBy(() -> memberService.login(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.MEMBER_LOGIN_UNREGISTER);
    }

    @Test
    @DisplayName("사용자 정보 찾기에 성공한 경우를 테스트 한다.")
    void findMemberSuccessTest() {
        // Given, test.sql
        Long memberId = 1L;

        // When, Then
        Member member = memberService.getMemberWithThrow(memberId);

        // Then
        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getNickname()).isEqualTo("User1");
        assertThat(member.getEmail()).isEqualTo("email1@test.com");
    }

    @Test
    @DisplayName("사용자 정보를 찾을 수 없는 경우를 테스트 한다.")
    void notFoundMemberTest() {
        // Given, test.sql
        Long memberId = 11L;

        // When, Then
        assertThatThrownBy(() -> memberService.getMemberWithThrow(memberId))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.NOT_FOUND_MEMBER);
    }

    @Test
    @DisplayName("내 프로필 조회에 성공한 경우를 테스트한다.")
    void findMyProfileSuccessTest() {
        // Given, test.sql
        Long memberId = 1L;
        Member member = memberService.getMemberWithThrow(memberId);

        // When
        MemberProfileResponse profile = memberService.getProfile(member, memberId);

        // Then
        MemberProfileResponse result = profile.result();
        assertThat(result.nickname()).isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("다른 사용자 프로필 조회에 성공한 경우를 테스트한다.")
    void findOtherProfileSuccessTest() {
        // Given, test.sql
        Long memberId = 1L;
        Member member = memberService.getMemberWithThrow(memberId);
        Long otherMemberId = 2L;
        Member other = memberService.getMemberWithThrow(otherMemberId);

        // When
        MemberProfileResponse profile = memberService.getProfile(member, otherMemberId);

        // Then
        MemberProfileResponse result = profile.result();
        assertThat(result.nickname()).isEqualTo(other.getNickname());
    }

    @Test
    @DisplayName("다른 사용자 프로필 조회에 성공한 경우를 테스트한다.")
    void imageUploadSuccessTest() {
        // Given
        Long memberId = 1L;
        MultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes()
        );

        // When
        ImageUploadResponse other = memberService.profileImageUpload(file, memberId);

        // Then
        ProfileImageUploadResponse result = other.result();
        assertThat(result.imageUrl()).contains(memberId.toString());
    }


    @Test
    @DisplayName("S3 이미지 업로드 시 이미지 파일이 아닐 경우 예외가 발생한다.")
    void InvalidImageUploadShouldBadRequestException() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "text/plain", "test image content".getBytes()
        );
        Long memberId = 1L;

        // When, Then
        assertThatThrownBy(() -> memberService.profileImageUpload(file, memberId))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", S3Exception.INVALID_IMAGE);
    }

}