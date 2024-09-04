package deepdivers.community.domain.member.controller.open;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.domain.token.dto.TokenResponse;
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
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ResponseEntity<NoContent> signUp(
        @Valid @RequestBody final MemberSignUpRequest request
    ) {
        final NoContent response = memberService.signUp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<API<TokenResponse>> login(
        @RequestBody final MemberLoginRequest request
    ) {
        final API<TokenResponse> response = memberService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate/nickname")
    public ResponseEntity<NoContent> validateNickname(@RequestParam final String nickname) {
        final NoContent response = memberService.validateUniqueNickname(nickname);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/validate/email")
    public ResponseEntity<NoContent> validateEmail(@RequestParam final String email) {
        NoContent response = memberService.validateUniqueEmail(email);
        return ResponseEntity.ok(response);
    }

}
