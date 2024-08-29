package deepdivers.community.domain.member.controller.api;

import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.global.security.jwt.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController implements MemberApiControllerDocs {

    private final MemberService memberService;

    @GetMapping("/{profileOwnerId}/me")
    public ResponseEntity<MemberProfileResponse> me(
            @Auth final Member member,
            @PathVariable final Long profileOwnerId
    ) {
        final MemberProfileResponse profile = memberService.getProfile(member, profileOwnerId);
        return ResponseEntity.ok(profile);
    }

}
