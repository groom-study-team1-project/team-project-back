package deepdivers.community.domain.member.service;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
import deepdivers.community.domain.member.dto.response.result.type.MemberResultType;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.Account;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.AccountRepository;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.utility.encryptor.Encryptor;
import deepdivers.community.utility.encryptor.EncryptorBean;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    @EncryptorBean
    private final Encryptor encryptor;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    public MemberSignUpResponse signUp(final MemberSignUpRequest request) {
        signUpValidate(request);
        final Member member = Member.registerMember(request.memberRegisterInfo());
        final Account account = Account.accountSignUp(request.memberAccountInfo(), encryptor, member);

        return MemberSignUpResponse.of(MemberResultType.MEMBER_SIGN_UP_SUCCESS, accountRepository.save(account));
    }

    private void signUpValidate(final MemberSignUpRequest request) {
        validateUniqueEmail(request.memberAccountInfo().email());
        validateUniqueNickname(request.memberRegisterInfo().nickname());
    }

    private void validateUniqueEmail(final String email) {
        final Boolean isDuplicateEmail = accountRepository.existsAccountByEmail(email);
        if (isDuplicateEmail) {
            throw new BadRequestException(MemberExceptionType.ALREADY_REGISTERED_EMAIL);
        }
    }

    private void validateUniqueNickname(final String nickname) {
        final Boolean isDuplicateNickname = memberRepository.existsMemberByNicknameValue(nickname);
        if (isDuplicateNickname) {
            throw new BadRequestException(MemberExceptionType.ALREADY_REGISTERED_NICKNAME);
        }
    }

}
