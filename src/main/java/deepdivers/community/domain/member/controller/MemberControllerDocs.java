package deepdivers.community.domain.member.controller;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "사용자", description = "사용자 관련 API")
public interface MemberControllerDocs {

    ResponseEntity<MemberSignUpResponse> signUp(MemberSignUpRequest request);

}
