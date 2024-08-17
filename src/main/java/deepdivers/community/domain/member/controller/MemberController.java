package deepdivers.community.domain.member.controller;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
import deepdivers.community.domain.member.model.Account;
import deepdivers.community.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ResponseEntity<MemberSignUpResponse> signUp(@Valid @RequestBody final MemberSignUpRequest request) {
        final MemberSignUpResponse response = memberService.signUp(request);
        return ResponseEntity.ok(response);
    }

}
