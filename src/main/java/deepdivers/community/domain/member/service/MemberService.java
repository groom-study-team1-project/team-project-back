package deepdivers.community.domain.member.service;

import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.dto.response.MemberLoginResponse;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
import deepdivers.community.domain.member.dto.response.result.ProfileImageUploadResult;
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
import deepdivers.community.utility.uploader.S3Uploader;
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

    public MemberSignUpResponse signUp(final MemberSignUpRequest request) {
        signUpValidate(request);
        final Member member = Member.of(request, encryptor);

        return MemberSignUpResponse.of(MemberStatusType.MEMBER_SIGN_UP_SUCCESS, memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public MemberLoginResponse login(final MemberLoginRequest request) {
        final Member member = authenticateMember(request.email(), request.password());
        validateMemberStatus(member);

        final TokenResponse tokenResponse = tokenService.tokenGenerator(member);
        return MemberLoginResponse.of(MemberStatusType.MEMBER_LOGIN_SUCCESS, tokenResponse);
    }

    @Transactional(readOnly = true)
    public Member getMemberWithThrow(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    public MemberProfileResponse profileImageUpload(final MultipartFile imageFile, final Long memberId) {
        final String uploadUrl = s3Uploader.profileImageUpload(imageFile, memberId);
        return MemberProfileResponse.of(MemberStatusType.UPLOAD_IMAGE_SUCCESS, uploadUrl);
    }

    private Member authenticateMember(final String email, final String password) {
        return memberRepository.findByEmail(email)
                .filter(member -> encryptor.matches(password, member.getPassword()))
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND_ACCOUNT));
    }

    private void validateMemberStatus(final Member member) {
        switch (member.getStatus()) {
            case REGISTERED:
                break;
            case DORMANCY:
                throw new BadRequestException(MemberExceptionType.MEMBER_LOGIN_DORMANCY);
            case UNREGISTERED:
                throw new BadRequestException(MemberExceptionType.MEMBER_LOGIN_UNREGISTER);
        }
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
