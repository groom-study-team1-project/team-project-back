package deepdivers.community.domain.member.controller.api;

import deepdivers.community.domain.member.dto.response.MemberProfileImageResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.global.security.jwt.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController implements MemberApiControllerDocs {

    private final MemberService memberService;
    
    @GetMapping("/{memberId}/me")
    public ResponseEntity<MemberProfileResponse> me(
            @Auth final Member member,
            @PathVariable final Long memberId
    ) {
        final MemberProfileResponse profile = memberService.getProfile(member, memberId);
        return ResponseEntity.ok(profile);
    }

    @PostMapping(value = "/profile-image", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<MemberProfileImageResponse> profileImageUpload(
            @Auth final Member member,
            @ModelAttribute final MultipartFile imageFile
    ) {
        final MemberProfileImageResponse response = memberService.profileImageUpload(imageFile, member.getId());
        return ResponseEntity.ok(response);
    }

}
