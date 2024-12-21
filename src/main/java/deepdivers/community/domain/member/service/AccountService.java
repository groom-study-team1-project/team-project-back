package deepdivers.community.domain.member.service;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.dto.request.AuthenticateEmailRequest;
import deepdivers.community.domain.member.dto.request.ResetPasswordRequest;
import deepdivers.community.domain.member.dto.request.VerifyEmailRequest;
import deepdivers.community.domain.member.dto.response.statustype.AccountStatusType;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
import deepdivers.community.infra.mail.MailHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final MailHelper mailHelper;
    private final MemberService memberService;

    public NoContent verifyNickname(final String nickname) {
        memberService.validateUniqueNickname(nickname);
        return NoContent.from(AccountStatusType.NICKNAME_VALIDATE_SUCCESS);
    }

    public NoContent verifyEmail(final VerifyEmailRequest request) {
        mailHelper.verifyEmail(request.email(), request.verifyCode());
        return NoContent.from(AccountStatusType.VERIFY_EMAIL_SUCCESS);
    }

    public NoContent emailAuthentication(final AuthenticateEmailRequest request) {
        boolean hasEmail = memberService.hasEmailVerification(request.email());
        if (hasEmail) {
            throw new BadRequestException(MemberExceptionType.ALREADY_REGISTERED_EMAIL);
        }
        mailHelper.sendAuthenticatedEmail(request.email());
        return NoContent.from(AccountStatusType.SEND_VERIFY_CODE_SUCCESS);
    }

    public NoContent passwordAuthentication(final AuthenticateEmailRequest request) {
        // todo test
        boolean hasEMail = memberService.hasEmailVerification(request.email());
        if (!hasEMail) {
            throw new NotFoundException(MemberExceptionType.NOT_FOUND_ACCOUNT);
        }
        mailHelper.sendAuthenticatedEmail(request.email());
        return NoContent.from(AccountStatusType.SEND_VERIFY_CODE_SUCCESS);
    }

    public NoContent resetPassword(final ResetPasswordRequest request) {
        final Member member = memberService.getMemberWithThrow(request.email());
        return memberService.resetPassword(member, request);
    }
}
