package deepdivers.community.domain.member.service;

import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.dto.request.AuthenticateEmailRequest;
import deepdivers.community.domain.member.dto.request.ResetPasswordRequest;
import deepdivers.community.domain.member.dto.request.VerifyEmailRequest;
import deepdivers.community.domain.member.dto.code.AccountStatusCode;
import deepdivers.community.domain.member.exception.MemberExceptionCode;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.common.exception.NotFoundException;
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
        return NoContent.from(AccountStatusCode.NICKNAME_VALIDATE_SUCCESS);
    }

    public NoContent verifyEmail(final VerifyEmailRequest request) {
        mailHelper.verifyEmail(request.email(), request.verifyCode());
        return NoContent.from(AccountStatusCode.VERIFY_EMAIL_SUCCESS);
    }

    public NoContent emailAuthentication(final AuthenticateEmailRequest request) {
        memberService.validateUniqueEmail(request.email());
        mailHelper.sendAuthenticatedEmail(request.email());
        return NoContent.from(AccountStatusCode.SEND_VERIFY_CODE_SUCCESS);
    }

    public NoContent passwordAuthentication(final AuthenticateEmailRequest request) {
        boolean hasEMail = memberService.hasEmailVerification(request.email());
        if (!hasEMail) {
            throw new NotFoundException(MemberExceptionCode.NOT_FOUND_ACCOUNT);
        }
        mailHelper.sendAuthenticatedEmail(request.email());
        return NoContent.from(AccountStatusCode.SEND_VERIFY_CODE_SUCCESS);
    }

    public NoContent resetPassword(final ResetPasswordRequest request) {
        final Member member = memberService.getMemberWithThrow(request.email());
        return memberService.resetPassword(member, request);
    }

}
