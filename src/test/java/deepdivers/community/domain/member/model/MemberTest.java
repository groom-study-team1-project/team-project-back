package deepdivers.community.domain.member.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.member.dto.request.info.MemberRegisterInfo;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.vo.MemberRole;
import deepdivers.community.domain.member.model.vo.MemberStatus;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberTest {

    @Test
    @DisplayName("올바른 사용자 정보 입력 시 Member 객체를 성공적으로 생성하는 것을 확인한다.")
    void fromWithValidInformationShouldCreateMember() {
        // given
        MemberRegisterInfo memberRegisterInfo = new MemberRegisterInfo("테스트", "이미지", "010-1234-5678");

        // when
        Member member = Member.registerMember(memberRegisterInfo);

        // then
        assertThat(member).isNotNull();
        assertThat(member.getNickname()).isEqualTo("테스트");
        assertThat(member.getImageUrl()).isEqualTo("이미지");
        assertThat(member.getContact().getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(member.getRole()).isEqualTo(MemberRole.NORMAL);
        assertThat(member.getStatus()).isEqualTo(MemberStatus.REGISTERED);
        assertThat(member.getAboutMe()).isEmpty();
        assertThat(member.getActivityStats()).isNotNull();
    }

    @Test
    @DisplayName("잘못된 전화번호 정보를 포함한 사용자 정보 입력 시 전화번호 형식 예외가 발생한다.")
    void fromWithInvalidPhoneNumberShouldThrowException() {
        // given
        MemberRegisterInfo memberRegisterInfo = new MemberRegisterInfo("테스트", "이미지", "010-0000-0000");

        // when, then
        assertThatThrownBy(() -> Member.registerMember(memberRegisterInfo))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.INVALID_PHONE_NUMBER_FORMAT);
    }

    @Test
    @DisplayName("잘못된 닉네임 정보를 포함한 사용자 정보 입력 시 닉네임 예외가 발생한다.")
    void fromWithInvalidNicknameShouldThrowException() {
        // given
        MemberRegisterInfo includedInvalidNicknameLength = new MemberRegisterInfo("", "이미지", "010-1234-5678");
        MemberRegisterInfo includedInvalidNicknameFormat = new MemberRegisterInfo("1 숫자로 시작하고 공백", "이미지", "010-1234-5678");

        // when, then
        assertThatThrownBy(() -> Member.registerMember(includedInvalidNicknameLength))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.INVALID_NICKNAME_LENGTH);
        assertThatThrownBy(() -> Member.registerMember(includedInvalidNicknameFormat))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.INVALID_NICKNAME_FORMAT);
    }

}