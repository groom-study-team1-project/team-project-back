package deepdivers.community.domain.member.controller.api;

import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.model.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "사용자", description = "사용자 관련 API")
public interface MemberApiControllerDocs {

    ResponseEntity<MemberProfileResponse> me(Member member, Long profileOwnerId);

}
