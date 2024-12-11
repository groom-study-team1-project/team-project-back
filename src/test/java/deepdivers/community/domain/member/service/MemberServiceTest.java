package deepdivers.community.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.common.StatusType;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.request.UpdatePasswordRequest;
import deepdivers.community.domain.member.dto.response.ImageUploadResponse;
import deepdivers.community.domain.member.dto.response.statustype.MemberStatusType;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.model.vo.MemberRole;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
import deepdivers.community.global.security.jwt.AuthHelper;
import deepdivers.community.global.security.jwt.AuthPayload;
import deepdivers.community.global.utility.encryptor.Encryptor;
import deepdivers.community.global.utility.encryptor.EncryptorBean;
import deepdivers.community.global.utility.uploader.S3Exception;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
@Import(LocalStackTestConfig.class)
@DirtiesContext
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private AuthHelper authHelper;
    @Autowired
    @EncryptorBean
    private Encryptor encryptor;
    @Autowired
    private MemberRepository memberRepository;

    /*
     * 회원 가입 관련 테스트
     * 성공, 예외
     * */
    @Test
    @DisplayName("회원 가입이 성공했을 경우 저장된 정보를 테스트한다.")
    void signUpSuccessAfterFindMemberTest() {
        // Given, test.sql
        String email = "test@mail.com";
        String password = "password1234!";
        String nickname = "test";
        String img = "test";
        String tel = "010-1234-5678";
        MemberSignUpRequest request = new MemberSignUpRequest(email, password, nickname, img, tel);
        long lastAccountId = 10L;

        // When
        memberService.signUp(request);

        // Then
        Member member = memberRepository.findByEmailValue(email).get();
        assertThat(member.getId()).isGreaterThan(lastAccountId);
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(encryptor.matches(password, member.getPassword())).isTrue();
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getPhoneNumber()).isEqualTo(tel);
    }

    @Test
    @DisplayName("소문자 닉네임 정보가 저장되는지 확인한다.")
    void validateSavedLowerCaseNickname() {
        // Given, Test.sql
        String nickname = "aA안1";
        MemberSignUpRequest request = new MemberSignUpRequest("test@mail.com", "password1234!", nickname, "test", "010-1234-5678");
        memberService.signUp(request);
        String expectedNickname = "aa안1";

        // When & Then
        assertThatThrownBy(() -> memberService.validateUniqueNickname(expectedNickname))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.ALREADY_REGISTERED_NICKNAME);
    }

    @Test
    @DisplayName("회원 가입이 성공했을 경우를 테스트한다.")
    void signUpSuccessTest() {
        // Given, test.sql
        MemberSignUpRequest request = new MemberSignUpRequest("test@mail.com", "password1234!", "test", "test", "010-1234-5678");

        // When
        NoContent response = memberService.signUp(request);

        // Then
        StatusType statusType = MemberStatusType.MEMBER_SIGN_UP_SUCCESS;
        assertThat(response).isNotNull();
        assertThat(response.status().code()).isEqualTo(statusType.getCode());
        assertThat(response.status().message()).isEqualTo(statusType.getMessage());
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

    /*
     * 로그인 관련 테스트
     * */
    @Test
    @DisplayName("로그인이 성공했을 경우를 테스트한다.")
    void loginSuccessTest() {
        // Given, test.sql
        MemberLoginRequest loginRequest = new MemberLoginRequest("email2@test.com", "password2!");

        // When
        API<TokenResponse> response = memberService.login(loginRequest);

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

    /*
     * 사용자 정보 찾기 관련 테스트
     * */
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

    /*
     * 프로필 수정 관련 테스트
     * */
    @Test
    @DisplayName("프로필 이미지 업로드에 성공한 경우를 테스트한다.")
    void imageUploadSuccessTest() {
        // Given
        Long memberId = 1L;
        MultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes()
        );

        // When
        API<ImageUploadResponse> other = memberService.profileImageUpload(file, memberId);

        // Then
        ImageUploadResponse result = other.result();
        assertThat(result.imageUrl()).contains(memberId.toString());
    }

    @Test
    @DisplayName("이미지 업로드 시 이미지 파일이 아닐 경우 예외가 발생한다.")
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

    @Test
    @DisplayName("프로필 수정이 성공할 경우를 테스트한다.")
    void profileUpdateSuccessTest() {
        // Given test.sql
        Member member = memberService.getMemberWithThrow(1L);
        MemberProfileRequest request = new MemberProfileRequest("test", "test", "", "010-1234-5678", "", "", "EMPTY");

        // When
        NoContent memberProfileResponseAPI = memberService.updateProfile(member, request);

        // then
        StatusResponse responseStatus = memberProfileResponseAPI.status();
        MemberStatusType status = MemberStatusType.UPDATE_PROFILE_SUCCESS;
        assertThat(responseStatus.code()).isEqualTo(status.getCode());
        assertThat(responseStatus.message()).isEqualTo(status.getMessage());
    }

    /*
     * 비밀번호 변경 서비스
     * */
    @Test
    @DisplayName("비밀번호 수정이 성공할 경우를 테스트한다.")
    void passwordUpdateSuccessTest() {
        // Given test.sql
        Member member = memberService.getMemberWithThrow(1L);
        UpdatePasswordRequest request = new UpdatePasswordRequest("password1!", "password2!");

        // When
        NoContent response = memberService.changePassword(member, request);

        // then
        StatusResponse responseStatus = response.status();
        MemberStatusType status = MemberStatusType.UPDATE_PASSWORD_SUCCESS;
        assertThat(responseStatus.code()).isEqualTo(status.getCode());
        assertThat(responseStatus.message()).isEqualTo(status.getMessage());
    }

    @Test
    @DisplayName("동일한 비밀번호로 수정할 경우 예외가 발생한다.")
    void samePasswordUpdateErrorTest() {
        // Given test.sql
        Member member = memberService.getMemberWithThrow(2L);
        UpdatePasswordRequest request = new UpdatePasswordRequest("password2!", "password2!");

        // When, then
        assertThatThrownBy(() -> memberService.changePassword(member, request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.ALREADY_USING_PASSWORD);
    }

    @Test
    @DisplayName("틀린 비밀번호로 수정할 경우 예외가 발생한다.")
    void wrongPasswordUpdateErrorTest() {
        // Given test.sql
        Member member = memberService.getMemberWithThrow(2L);
        UpdatePasswordRequest request = new UpdatePasswordRequest("password3!", "password2!");

        // When, then
        assertThatThrownBy(() -> memberService.changePassword(member, request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.INVALID_PASSWORD);
    }

    /*
     * 부가 서비스
     * */
    @Test
    @DisplayName("중복 닉네임은 예외가 발생한다.")
    void duplicateNicknameCheckTest() {
        // Given, Test.sql
        String nickname = "User9";

        // When & Then
        assertThatThrownBy(() -> memberService.validateUniqueNickname(nickname))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.ALREADY_REGISTERED_NICKNAME);
    }

    @Test
    @DisplayName("중복되지 않은 닉네임은 성공하는 경우를 테스트한다.")
    void NicknameCheckSuccessTest() {
        // given
        String nickname = "안녕하세요";

        // when
        assertThatCode(() -> memberService.validateUniqueNickname(nickname))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("닉네임 확인은 대소문자를 구분하지 않는다.")
    void duplicateLowerCaseNicknameCheckTest() {
        // Given, Test.sql
        String nickname = "user9";

        // When & Then
        assertThatThrownBy(() -> memberService.validateUniqueNickname(nickname))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.ALREADY_REGISTERED_NICKNAME);
    }


    @Test
    @DisplayName("존재하는 이메일일 경우 True를 반환한다.")
    void givenSignedEmailWhenVerificationThenTrue() {
        // Given test.sql
        String email = "email1@test.com";

        // when
        boolean hasEmail = memberService.hasEmailVerification(email);

        // then
        assertThat(hasEmail).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이메일일 경우 False를 반환한다.")
    void givenNotSignedEmailWhenVerificationThenTrue() {
        // Given test.sql
        String email = "email1@test.com";

        // when
        boolean hasEmail = memberService.hasEmailVerification(email);

        // then
        assertThat(hasEmail).isTrue();
    }

}