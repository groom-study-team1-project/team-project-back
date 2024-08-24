package deepdivers.community.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.common.StatusType;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
import deepdivers.community.domain.member.dto.response.result.MemberSignUpResult;
import deepdivers.community.domain.member.dto.response.result.type.MemberStatusType;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입이 성공했을 경우를 통합 테스트한다.")
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
        MemberSignUpResult responseResult = response.result();
        assertThat(response).isNotNull();
        assertThat(response.status().code()).isEqualTo(statusType.getCode());
        assertThat(response.status().message()).isEqualTo(statusType.getMessage());
        assertThat(responseResult.id()).isEqualTo(lastAccountId + 1L);
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
    @DisplayName("중복 닉네임으로 회원 가입 시 예외 발생 테스트")
    void signUpDuplicateNicknameTest() {
        // Given
        MemberSignUpRequest request = new MemberSignUpRequest("test@mail.com", "password123!", "User9", "test", "010-1234-5678");

        // When & Then
        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.ALREADY_REGISTERED_NICKNAME);
    }

}