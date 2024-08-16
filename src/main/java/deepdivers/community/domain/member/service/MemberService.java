package deepdivers.community.domain.member.service;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
import deepdivers.community.domain.member.dto.response.result.type.SignUpResultType;
import deepdivers.community.domain.member.model.Account;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.AccountRepository;
import deepdivers.community.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    public MemberSignUpResponse signUp(final MemberSignUpRequest request) {
        final Member member = Member.registerMember(request.memberRegisterInfo());
        final Account account = Account.accountSignUp(request.memberAccountInfo(), member);

        return MemberSignUpResponse.of(SignUpResultType.MEMBER_SIGN_UP_SUCCESS, accountRepository.save(account));
    }

}
