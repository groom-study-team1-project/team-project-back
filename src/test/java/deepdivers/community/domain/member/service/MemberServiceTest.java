package deepdivers.community.domain.member.service;

import deepdivers.community.domain.common.ResultType;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.request.info.MemberAccountInfo;
import deepdivers.community.domain.member.dto.request.info.MemberRegisterInfo;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
import deepdivers.community.domain.member.dto.response.result.MemberSignUpResult;
import deepdivers.community.domain.member.dto.response.result.type.MemberResultType;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.Account;
import deepdivers.community.domain.member.repository.AccountRepository;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입이 성공했을 경우를 통합 테스트한다.")
    void signUpSuccessTest() {
        // Given, test.sql
        MemberAccountInfo accountInfo = new MemberAccountInfo("test@mail.com", "password1234!");
        MemberRegisterInfo registerInfo = new MemberRegisterInfo("test", "test", "010-1234-5678");
        MemberSignUpRequest request = new MemberSignUpRequest(accountInfo, registerInfo);
        long lastAccountId = 10L;
        LocalDateTime testStartTime = LocalDateTime.now();

        // When
        MemberSignUpResponse response = memberService.signUp(request);

        // Then
        LocalDateTime testEndTime = LocalDateTime.now();
        ResultType resultType = MemberResultType.MEMBER_SIGN_UP_SUCCESS;
        MemberSignUpResult responseResult = response.result();
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(resultType.getCode());
        assertThat(response.message()).isEqualTo(resultType.getMessage());
        assertThat(responseResult.id()).isEqualTo(lastAccountId + 1L);
        assertThat(responseResult.nickname()).isEqualTo(registerInfo.nickname());
        assertThat(responseResult.createdAt()).isBetween(testStartTime, testEndTime);
    }

    @Test
    @DisplayName("중복 이메일로 회원 가입 시 예외 발생하는 경우를 테스트한다.")
    void signUpDuplicateEmailTest() {
        // Given test.sql
        MemberAccountInfo accountInfo = new MemberAccountInfo("email1@test.com", "password1!");
        MemberRegisterInfo registerInfo = new MemberRegisterInfo("test", "test", "010-1234-5678");
        MemberSignUpRequest request = new MemberSignUpRequest(accountInfo, registerInfo);

        // When & Then
        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.ALREADY_REGISTERED_EMAIL);
    }

    @Test
    @DisplayName("중복 닉네임으로 회원 가입 시 예외 발생 테스트")
    void signUpDuplicateNicknameTest() {
        // Given
        MemberAccountInfo accountInfo = new MemberAccountInfo("test@mail.com", "password123!");
        MemberRegisterInfo registerInfo = new MemberRegisterInfo("User9", "test", "010-1234-5678");
        MemberSignUpRequest request = new MemberSignUpRequest(accountInfo, registerInfo);

        // When & Then
        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.ALREADY_REGISTERED_NICKNAME);
    }

}