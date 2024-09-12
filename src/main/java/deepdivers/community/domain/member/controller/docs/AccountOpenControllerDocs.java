package deepdivers.community.domain.member.controller.docs;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.dto.request.VerifyEmailRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "3. 회원 계정", description = "회원 계정 관련 API")
public interface AccountOpenControllerDocs {
    ResponseEntity<NoContent> verifyEmail(VerifyEmailRequest request);
}
