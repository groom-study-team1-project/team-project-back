package deepdivers.community.domain.member.service;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.request.ResetPasswordRequest;
import deepdivers.community.domain.member.dto.request.UpdatePasswordRequest;
import deepdivers.community.domain.member.dto.code.MemberStatusCode;
import deepdivers.community.domain.member.exception.MemberExceptionCode;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.member.repository.jpa.MemberRepository;
import deepdivers.community.domain.token.dto.response.TokenResponse;
import deepdivers.community.domain.token.service.TokenService;
import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.common.exception.NotFoundException;
import deepdivers.community.global.utility.encryptor.PasswordEncryptor;
import deepdivers.community.infra.aws.s3.S3PresignManager;
import deepdivers.community.infra.aws.s3.S3TagManager;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final PasswordEncryptor passwordEncryptor;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final S3TagManager s3TagManager;
    private final S3PresignManager s3PresignManager;

    public NoContent signUp(final MemberSignUpRequest request) {
        signUpValidate(request);

        final Member member = Member.of(request, passwordEncryptor);
        useProfileImage(member, request.imageKey());
        memberRepository.save(member);

        return NoContent.from(MemberStatusCode.MEMBER_SIGN_UP_SUCCESS);
    }

    public void useProfileImage(final Member member, final String imageKey) {
        if (imageKey == null) {
            return;
        }

        s3TagManager.markAsDeleted(member.getImage().getImageKey());
        s3TagManager.removeDeleteTag(imageKey);
        member.updateProfileImage(imageKey, s3PresignManager.generateAccessUrl(imageKey));
    }

    @Transactional(readOnly = true)
    public API<TokenResponse> login(final MemberLoginRequest request) {
        final Member member = authenticateMember(request.email(), request.password());
        member.validateStatus();

        final TokenResponse tokenResponse = tokenService.generateToken(member);
        return API.of(MemberStatusCode.MEMBER_LOGIN_SUCCESS, tokenResponse);
    }

    @Transactional(readOnly = true)
    public Member getMemberWithThrow(final Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new NotFoundException(MemberExceptionCode.NOT_FOUND_MEMBER));
    }

    @Transactional(readOnly = true)
    public Member getMemberWithThrow(final String email) {
        return memberRepository.findByEmailValue(email)
            .orElseThrow(() -> new NotFoundException(MemberExceptionCode.NOT_FOUND_MEMBER));
    }

    public NoContent updateProfile(final Member member, final MemberProfileRequest request) {
        validateNewNickname(member.getNickname(), request.nickname());

        member.updateProfile(request);
        useProfileImage(member, request.imageKey());

        return NoContent.from(MemberStatusCode.UPDATE_PROFILE_SUCCESS);
    }

    private void validateNewNickname(final String memberNickname, final String newNickname) {
        if (!memberNickname.equals(newNickname)) {
            validateUniqueNickname(newNickname);
        }
    }

    public NoContent changePassword(final Member member, final UpdatePasswordRequest request) {
        member.changePassword(passwordEncryptor, request);
        memberRepository.save(member);
        return NoContent.from(MemberStatusCode.UPDATE_PASSWORD_SUCCESS);
    }

    protected NoContent resetPassword(final Member member, final ResetPasswordRequest request) {
        // todo test
        member.resetPassword(passwordEncryptor, request.password());
        memberRepository.save(member);
        return NoContent.from(MemberStatusCode.UPDATE_PASSWORD_SUCCESS);
    }

    private Member authenticateMember(final String email, final String password) {
        return memberRepository.findByEmailValue(email)
            .filter(member -> passwordEncryptor.matches(password, member.getPassword()))
            .orElseThrow(() -> new NotFoundException(MemberExceptionCode.NOT_FOUND_ACCOUNT));
    }

    private void signUpValidate(final MemberSignUpRequest request) {
        validateUniqueEmail(request.email());
        validateUniqueNickname(request.nickname());
    }

    protected void validateUniqueEmail(final String email) {
        if (hasEmailVerification(email)) {
            throw new BadRequestException(MemberExceptionCode.ALREADY_REGISTERED_EMAIL);
        }
    }

    protected boolean hasEmailVerification(final String email) {
        return memberRepository.existsByEmailValue(email);
    }

    protected void validateUniqueNickname(final String nickname) {
        final String lowerNickname = nickname.toLowerCase(Locale.ENGLISH);
        final boolean isDuplicateNickname = memberRepository.existsByNicknameLower(lowerNickname);

        if (isDuplicateNickname) {
            throw new BadRequestException(MemberExceptionCode.ALREADY_REGISTERED_NICKNAME);
        }
    }

}
