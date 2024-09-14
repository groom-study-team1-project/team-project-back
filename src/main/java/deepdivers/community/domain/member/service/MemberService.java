package deepdivers.community.domain.member.service;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.ImageUploadResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.dto.response.statustype.MemberStatusType;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.model.Email;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.model.Nickname;
import deepdivers.community.domain.member.model.PhoneNumber;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.domain.token.service.TokenService;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.NotFoundException;
import deepdivers.community.utility.encryptor.Encryptor;
import deepdivers.community.utility.encryptor.EncryptorBean;
import deepdivers.community.utility.uploader.S3Uploader;
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

    public NoContent signUp(final MemberSignUpRequest request) {
        signUpValidate(request);

        final Member member = Member.of(request, encryptor);
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
    public API<MemberProfileResponse> getProfile(final Member me, final Long memberId) {
        final Member profileOwner = getMemberWithThrow(memberId);
        if (me.equals(profileOwner)) {
            final MemberProfileResponse result = MemberProfileResponse.from(me);
            return API.of(MemberStatusType.VIEW_OWN_PROFILE_SUCCESS, result);
        }

        final MemberProfileResponse result = MemberProfileResponse.from(profileOwner);
        return API.of(MemberStatusType.VIEW_OTHER_PROFILE_SUCCESS, result);
    }

    @Transactional(readOnly = true)
    public Member getMemberWithThrow(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    public API<ImageUploadResponse> profileImageUpload(final MultipartFile imageFile, final Long memberId) {
        final String uploadUrl = s3Uploader.profileImageUpload(imageFile, memberId);
        return API.of(MemberStatusType.UPLOAD_IMAGE_SUCCESS, ImageUploadResponse.of(uploadUrl));
    }

    public API<MemberProfileResponse> updateProfile(final Long memberId, final MemberProfileRequest request) {
        final Member member = getMemberWithThrow(memberId);
        updateAfterProfileValidation(member, request);

        final Member updatedMember = memberRepository.save(member);
        final MemberProfileResponse result = MemberProfileResponse.from(updatedMember);
        return API.of(MemberStatusType.UPDATE_PROFILE_SUCCESS, result);
    }

    public NoContent verifyNickname(final String nickname) {
        validateUniqueNickname(nickname);
        return NoContent.from(MemberStatusType.NICKNAME_VALIDATE_SUCCESS);
    }

    private Member authenticateMember(final String email, final String password) {
        return memberRepository.findByEmailValue(email)
                .filter(member -> encryptor.matches(password, member.getPassword()))
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND_ACCOUNT));
    }

    private void updateAfterProfileValidation(final Member member, final MemberProfileRequest request) {
        if (!member.getNickname().equals(request.nickname())) {
            validateUniqueNickname(request.nickname());
        }
        Nickname.validator(request.nickname());
        PhoneNumber.validator(request.phoneNumber());
        member.updateProfile(request);
    }

    private void signUpValidate(final MemberSignUpRequest request) {
        validateUniqueEmail(request.email());
        validateUniqueNickname(request.nickname());
        Email.validator(request.email());
        Nickname.validator(request.nickname());
        PhoneNumber.validator(request.phoneNumber());
    }

    protected void validateUniqueEmail(final String email) {
        final Boolean isDuplicateEmail = memberRepository.existsByEmailValue(email);
        if (isDuplicateEmail) {
            throw new BadRequestException(MemberExceptionType.ALREADY_REGISTERED_EMAIL);
        }
    }

    protected void validateUniqueNickname(final String nickname) {
        final String lowerNickname = nickname.toLowerCase(Locale.ENGLISH);
        final boolean isDuplicateNickname = memberRepository.existsByLowerNickname(lowerNickname);

        if (isDuplicateNickname) {
            throw new BadRequestException(MemberExceptionType.ALREADY_REGISTERED_NICKNAME);
        }
    }

}
