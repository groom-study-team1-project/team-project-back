package deepdivers.community.domain.member.controller;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.controller.docs.MemberOpenControllerDocs;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.dto.code.MemberStatusType;
import deepdivers.community.domain.member.controller.interfaces.MemberQueryRepository;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.domain.token.dto.response.TokenResponse;
import deepdivers.community.global.security.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open/members")
@RequiredArgsConstructor
public class MemberOpenController implements MemberOpenControllerDocs {

    private final MemberService memberService;
    private final MemberQueryRepository memberQueryRepository;

    @PostMapping("/sign-up")
    public ResponseEntity<NoContent> signUp(
        @Valid @RequestBody final MemberSignUpRequest request
    ) {
        final NoContent response = memberService.signUp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<API<TokenResponse>> login(
        @RequestBody @Valid final MemberLoginRequest request
    ) {
        final API<TokenResponse> response = memberService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/{memberId}")
    public ResponseEntity<API<MemberProfileResponse>> me(
        @PathVariable final Long memberId,
        @Auth final Long viewerId
    ) {
        final MemberProfileResponse memberProfile = memberQueryRepository.getMemberProfile(memberId, viewerId);
        return ResponseEntity.ok(API.of(MemberStatusType.GET_PROFILE_SUCCESS, memberProfile));
    }

}
