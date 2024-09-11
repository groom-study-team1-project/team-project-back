package deepdivers.community.domain.email.controller;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.email.EmailService;
import deepdivers.community.domain.email.dto.VerifyEmailRequest;
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
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/verify")
    public ResponseEntity<NoContent> sendEmail(@RequestBody @Valid final VerifyEmailRequest request) {
        final NoContent response = emailService.sendAuthenticatedEmail(request);
        return ResponseEntity.ok(response);
    }

}
