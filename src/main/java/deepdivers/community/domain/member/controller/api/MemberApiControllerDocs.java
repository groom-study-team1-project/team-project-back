package deepdivers.community.domain.member.controller.api;

import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.global.security.jwt.Auth;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자", description = "사용자 관련 API")
public interface MemberApiControllerDocs {

    ResponseEntity<MemberProfileResponse> profileImageUpload(Member member, MultipartFile imageFile);

}
