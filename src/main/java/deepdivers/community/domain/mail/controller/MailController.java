package deepdivers.community.domain.mail.controller;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.mail.MailService;
import deepdivers.community.domain.mail.dto.AuthenticateEmailRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mails")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/authenticate/emails")
    public ResponseEntity<NoContent> sendEmail(@RequestBody @Valid final AuthenticateEmailRequest request) {
        final NoContent response = mailService.sendAuthenticatedEmail(request);
        return ResponseEntity.ok(response);
    }

}
