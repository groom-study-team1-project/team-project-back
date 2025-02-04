package deepdivers.community.domain.member.controller;

import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.controller.docs.AccountOpenControllerDocs;
import deepdivers.community.domain.member.dto.request.AuthenticateEmailRequest;
import deepdivers.community.domain.member.dto.request.ResetPasswordRequest;
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
@RequestMapping("/open/accounts")
@RequiredArgsConstructor
public class AccountOpenController implements AccountOpenControllerDocs {

    private final AccountService accountService;

    @GetMapping("/verify/nickname")
    public ResponseEntity<NoContent> verifyNickname(@RequestParam final String nickname) {
        final NoContent response = accountService.verifyNickname(nickname);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify/email")
    public ResponseEntity<NoContent> verifyEmail(@RequestBody @Valid final VerifyEmailRequest request) {
        final NoContent response = accountService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate/email")
    public ResponseEntity<NoContent> sendEmailMail(@RequestBody @Valid final AuthenticateEmailRequest request) {
        final NoContent response = accountService.emailAuthentication(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate/password")
    public ResponseEntity<NoContent> sendPasswordMail(@RequestBody @Valid final AuthenticateEmailRequest request) {
        NoContent response = accountService.passwordAuthentication(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset/password")
    public ResponseEntity<NoContent> resetPassword(@RequestBody @Valid final ResetPasswordRequest request) {
        // todo 보안성 강화 - 토큰
        NoContent response = accountService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

}
