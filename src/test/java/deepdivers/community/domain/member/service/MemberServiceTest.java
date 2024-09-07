package deepdivers.community.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.common.StatusType;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.ImageUploadResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.dto.response.statustype.MemberStatusType;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.model.vo.MemberRole;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
import deepdivers.community.global.security.jwt.AuthHelper;
import deepdivers.community.global.security.jwt.AuthPayload;
import deepdivers.community.utility.encryptor.Encryptor;
import deepdivers.community.utility.encryptor.EncryptorBean;
import deepdivers.community.utility.uploader.S3Exception;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
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
    @DisplayName("회원 가입이 성공했을 경우를 테스트한다.")
    void signUpSuccessTest() {
        // Given, test.sql
        MemberSignUpRequest request = new MemberSignUpRequest("test@mail.com", "password1234!", "test", "test", "010-1234-5678");
        long lastAccountId = 10L;
        LocalDateTime testStartTime = LocalDateTime.now();

        // When
        NoContent response = memberService.signUp(request);

        // Then
        LocalDateTime testEndTime = LocalDateTime.now();
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
    @DisplayName("닉네임은 대소문자를 구분하지 않는다.")
    void duplicateLowerCaseNicknameCheckTest() {
        // Given, Test.sql
        String nickname = "user9";

        // When & Then
        assertThatThrownBy(() -> memberService.validateUniqueNickname(nickname))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.ALREADY_REGISTERED_NICKNAME);
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
        API<MemberProfileResponse> profile = memberService.getProfile(member, memberId);

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
        API<MemberProfileResponse> profile = memberService.getProfile(member, otherMemberId);

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
        API<ImageUploadResponse> other = memberService.profileImageUpload(file, memberId);

        // Then
        ImageUploadResponse result = other.result();
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

    @Test
    @DisplayName("중복된 이메일로 검증 시 예외가 발생한다.")
    void DuplicateEmailValidationTest() {
        // Given test.sql
        String email = "email1@test.com";

        // When & Then
        assertThatThrownBy(() -> memberService.validateUniqueEmail(email))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.ALREADY_REGISTERED_EMAIL);
    }

    @Test
    @DisplayName("올바른 이메일로 검증할 경우를 테스트한다.")
    void DuplicateEmailSuccessTest() {
        // Given test.sql
        String email = "email@test.com";

        // When
        NoContent result = memberService.validateUniqueEmail(email);

        // then
        MemberStatusType status = MemberStatusType.EMAIL_VALIDATE_SUCCESS;
        assertThat(result.status().code()).isEqualTo(status.getCode());
        assertThat(result.status().message()).isEqualTo(status.getMessage());
    }

}