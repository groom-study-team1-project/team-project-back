package deepdivers.community.domain.member.service;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.dto.request.AuthenticateEmailRequest;
import deepdivers.community.domain.member.dto.request.VerifyEmailRequest;
import deepdivers.community.domain.member.dto.response.statustype.AccountStatusType;
import deepdivers.community.domain.member.dto.response.statustype.MemberStatusType;
import deepdivers.community.global.mail.MailHelper;
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
        return NoContent.from(MemberStatusType.NICKNAME_VALIDATE_SUCCESS);
    }

    public NoContent verifyEmail(final VerifyEmailRequest request) {
        mailHelper.verifyEmail(request.email(), request.verifyCode());
        return NoContent.from(AccountStatusType.VERIFY_EMAIL_SUCCESS);
    }

    public NoContent sendAuthenticatedEmail(final AuthenticateEmailRequest request) {
        memberService.validateUniqueEmail(request.email());
        mailHelper.sendAuthenticatedEmail(request.email());
        return NoContent.from(AccountStatusType.SEND_VERIFY_CODE_SUCCESS);
    }

}
