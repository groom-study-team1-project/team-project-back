package deepdivers.community.domain.member.controller.open;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.controller.docs.AccountOpenControllerDocs;
import deepdivers.community.domain.member.dto.request.AuthenticateEmailRequest;
import deepdivers.community.domain.member.dto.request.VerifyEmailRequest;
import deepdivers.community.domain.member.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountOpenController implements AccountOpenControllerDocs {

    private final AccountService accountService;

    @GetMapping("/verify/nicknames")
    public ResponseEntity<NoContent> verifyNickname(@RequestParam final String nickname) {
        final NoContent response = accountService.verifyNickname(nickname);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify/emails")
    public ResponseEntity<NoContent> verifyEmail(@RequestBody @Valid final VerifyEmailRequest request) {
        final NoContent response = accountService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate/emails")
    public ResponseEntity<NoContent> sendEmail(@RequestBody @Valid final AuthenticateEmailRequest request) {
        final NoContent response = accountService.sendAuthenticatedEmail(request);
        return ResponseEntity.ok(response);
    }

}
