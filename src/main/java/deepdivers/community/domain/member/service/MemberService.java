package deepdivers.community.domain.member.service;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.request.ResetPasswordRequest;
import deepdivers.community.domain.member.dto.request.UpdatePasswordRequest;
import deepdivers.community.domain.member.dto.response.ImageUploadResponse;
import deepdivers.community.domain.member.dto.response.statustype.MemberStatusType;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.domain.token.service.TokenService;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
import deepdivers.community.global.utility.encryptor.Encryptor;
import deepdivers.community.global.utility.encryptor.EncryptorBean;
import deepdivers.community.infra.aws.s3.S3TagManager;
import deepdivers.community.infra.aws.s3.S3Uploader;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    @EncryptorBean
    private final Encryptor encryptor;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final S3Uploader s3Uploader;
    private final S3TagManager s3TagManager;

    public NoContent signUp(final MemberSignUpRequest request) {
        signUpValidate(request);

        final Member member = Member.of(request, encryptor);
        s3TagManager.removeDeleteTag(request.imageKey());
        memberRepository.save(member);

        return NoContent.from(MemberStatusType.MEMBER_SIGN_UP_SUCCESS);
    }

    @Transactional(readOnly = true)
    public API<TokenResponse> login(final MemberLoginRequest request) {
        final Member member = authenticateMember(request.email(), request.password());
        member.validateStatus();

        final TokenResponse tokenResponse = tokenService.tokenGenerator(member);
        return API.of(MemberStatusType.MEMBER_LOGIN_SUCCESS, tokenResponse);
    }

    @Transactional(readOnly = true)
    public Member getMemberWithThrow(final Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    @Transactional(readOnly = true)
    public Member getMemberWithThrow(final String email) {
        return memberRepository.findByEmailValue(email)
            .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    public API<ImageUploadResponse> profileImageUpload(final MultipartFile imageFile, final Long memberId) {
        final String uploadUrl = s3Uploader.profileImageUpload(imageFile, memberId);
        return API.of(MemberStatusType.UPLOAD_IMAGE_SUCCESS, ImageUploadResponse.of(uploadUrl));
    }

    public NoContent updateProfile(final Member member, final MemberProfileRequest request) {
        validateNewNickname(member.getNickname(), request.nickname());

        s3TagManager.removeDeleteTag(request.imageKey());
        s3TagManager.markAsDeleted(member.getImageKey());

        member.updateProfile(request);
        memberRepository.save(member);

        return NoContent.from(MemberStatusType.UPDATE_PROFILE_SUCCESS);
    }

    private void validateNewNickname(final String memberNickname, final String newNickname) {
        if (memberNickname.equals(newNickname)) {
            throw new BadRequestException(MemberExceptionType.ALREADY_REGISTERED_EMAIL);
        }
        validateUniqueNickname(newNickname);
    }

    public NoContent changePassword(final Member member, final UpdatePasswordRequest request) {
        member.changePassword(encryptor, request);
        memberRepository.save(member);
        return NoContent.from(MemberStatusType.UPDATE_PASSWORD_SUCCESS);
    }

    protected NoContent resetPassword(final Member member, final ResetPasswordRequest request) {
        // todo test
        member.resetPassword(encryptor, request.password());
        memberRepository.save(member);
        return NoContent.from(MemberStatusType.UPDATE_PASSWORD_SUCCESS);
    }

    private Member authenticateMember(final String email, final String password) {
        return memberRepository.findByEmailValue(email)
            .filter(member -> encryptor.matches(password, member.getPassword()))
            .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND_ACCOUNT));
    }

    private void signUpValidate(final MemberSignUpRequest request) {
        validateUniqueEmail(request.email());
        validateUniqueNickname(request.nickname());
    }

    private void validateUniqueEmail(final String email) {
        if (hasEmailVerification(email)) {
            throw new BadRequestException(MemberExceptionType.ALREADY_REGISTERED_EMAIL);
        }
    }

    protected boolean hasEmailVerification(final String email) {
        return memberRepository.existsByEmailValue(email);
    }

    protected void validateUniqueNickname(final String nickname) {
        final String lowerNickname = nickname.toLowerCase(Locale.ENGLISH);
        final boolean isDuplicateNickname = memberRepository.existsByLowerNickname(lowerNickname);

        if (isDuplicateNickname) {
            throw new BadRequestException(MemberExceptionType.ALREADY_REGISTERED_NICKNAME);
        }
    }

}
