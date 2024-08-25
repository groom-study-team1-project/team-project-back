package deepdivers.community.domain.member.service;

import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberLoginResponse;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
import deepdivers.community.domain.member.dto.response.result.type.MemberStatusType;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.domain.token.service.TokenService;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
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
    private final TokenService tokenService;

    public MemberSignUpResponse signUp(final MemberSignUpRequest request) {
        signUpValidate(request);
        final Member member = Member.of(request, encryptor);

        return MemberSignUpResponse.of(MemberStatusType.MEMBER_SIGN_UP_SUCCESS, memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public MemberLoginResponse login(final MemberLoginRequest request) {
        final Member member = memberRepository.findByEmail(request.email())
                .filter(it -> encryptor.matches(request.password(), it.getPassword()))
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND_ACCOUNT));

        final TokenResponse tokenResponse = tokenService.login(member);

        return switch (member.getStatus()) {
            case REGISTERED -> MemberLoginResponse.of(MemberStatusType.MEMBER_LOGIN_SUCCESS, tokenResponse);
            case DORMANCY -> throw new BadRequestException(MemberExceptionType.MEMBER_LOGIN_DORMANCY);
            case UNREGISTERED -> throw new BadRequestException(MemberExceptionType.MEMBER_LOGIN_UNREGISTER);
        };
    }

    private void signUpValidate(final MemberSignUpRequest request) {
        validateUniqueEmail(request.email());
        validateUniqueNickname(request.nickname());
    }

    private void validateUniqueEmail(final String email) {
        final Boolean isDuplicateEmail = memberRepository.existsAccountByEmail(email);
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
