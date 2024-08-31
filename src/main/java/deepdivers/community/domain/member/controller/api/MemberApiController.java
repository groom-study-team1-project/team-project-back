package deepdivers.community.domain.member.controller.api;

import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.global.security.jwt.Auth;
import deepdivers.community.utility.uploader.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController implements MemberApiControllerDocs {

    private final MemberService memberService;

    @PostMapping(value = "/profile-image", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<MemberProfileResponse> profileImageUpload(
            @Auth final Member member,
            @ModelAttribute final MultipartFile imageFile
    ) {
        final MemberProfileResponse response = memberService.profileImageUpload(imageFile, member.getId());
        return ResponseEntity.ok(response);
    }

}
