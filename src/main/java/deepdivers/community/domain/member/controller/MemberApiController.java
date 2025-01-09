package deepdivers.community.domain.member.controller;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.controller.docs.MemberApiControllerDocs;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.UpdatePasswordRequest;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.global.security.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController implements MemberApiControllerDocs {

    private final MemberService memberService;

    @PutMapping("/me")
    public ResponseEntity<NoContent> updateProfile(
        @Auth final Member member,
        @Valid @RequestBody final MemberProfileRequest request
    ) {
        final NoContent response = memberService.updateProfile(member, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<NoContent> updatePassword(
        @Auth final Member member,
        @RequestBody @Valid final UpdatePasswordRequest request
    ) {
        final NoContent response = memberService.changePassword(member, request);
        return ResponseEntity.ok(response);
    }

}
