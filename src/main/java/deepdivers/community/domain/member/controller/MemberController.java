package deepdivers.community.domain.member.controller;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    public ResponseEntity<String> login(final String email, final String password) {
        final Member member = memberService.login(email, password);
        return switch (member.getStatus()) {
            case REGISTERED -> ResponseEntity.ok(String.valueOf(member.getId()));
            case DORMANCY -> ResponseEntity.ok("휴면 회원입니다.");
            case UNREGISTERED -> ResponseEntity.ok("삭제 처리중입니다.");
        };
    }

}
