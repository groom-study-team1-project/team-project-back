package deepdivers.community.domain.member.service;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.model.Account;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.AccountRepository;
import deepdivers.community.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    public Account signUp(final MemberSignUpRequest request) {
        final Member member = Member.registerMember(request.memberInfo());
        final Account account = Account.accountSignUp(request.memberAccount(), member);

        return accountRepository.save(account);
    }

}
