package deepdivers.community.domain.member.controller.api;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.member.dto.response.ImageUploadResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.global.security.jwt.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController implements MemberApiControllerDocs {

    private final MemberService memberService;

    @GetMapping("/{memberId}/me")
    public ResponseEntity<API<MemberProfileResponse>> me(
            @Auth final Member member,
            @PathVariable final Long memberId
    ) {
        final API<MemberProfileResponse> response = memberService.getProfile(member, memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/profile-image", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<API<ImageUploadResponse>> profileImageUpload(
            @Auth final Member member,
            @RequestParam final MultipartFile imageFile
    ) {
        final API<ImageUploadResponse> response = memberService.profileImageUpload(imageFile, member.getId());
        return ResponseEntity.ok(response);
    }

}
