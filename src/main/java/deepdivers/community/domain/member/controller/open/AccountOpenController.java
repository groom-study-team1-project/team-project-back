package deepdivers.community.domain.member.controller.open;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.controller.docs.AccountOpenControllerDocs;
import deepdivers.community.domain.member.dto.request.VerifyEmailRequest;
import deepdivers.community.domain.member.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountOpenController implements AccountOpenControllerDocs {

    private final AccountService accountService;

    @PostMapping("/verify/emails")
    public ResponseEntity<NoContent> verifyEmail(@RequestBody @Valid final VerifyEmailRequest request) {
        final NoContent response = accountService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

}
