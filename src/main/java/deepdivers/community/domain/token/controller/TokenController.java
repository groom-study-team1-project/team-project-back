package deepdivers.community.domain.token.controller;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.domain.token.exception.TokenExceptionType;
import deepdivers.community.domain.token.service.TokenService;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.security.jwt.Auth;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @GetMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestHeader("Refresh-Token") final String refreshToken,
            @Auth final Member member
    ) {
        if (Objects.isNull(refreshToken)) {
            throw new BadRequestException(TokenExceptionType.NOT_FOUND_TOKEN);
        }
        final TokenResponse response = tokenService.reIssueAccessToken(member, refreshToken);
        return ResponseEntity.ok(response);
    }

}
