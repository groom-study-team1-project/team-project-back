package deepdivers.community.domain.token.controller;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.domain.token.service.TokenService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open/tokens")
@RequiredArgsConstructor
public class TokenController implements TokenControllerDocs {

    private final TokenService tokenService;

    @PatchMapping("/re-issue")
    public ResponseEntity<API<TokenResponse>> reIssue(
            @Parameter(hidden = true)
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            final String accessToken,
            @Parameter(hidden = true)
            @RequestHeader(value = "Refresh-Token", required = false)
            final String refreshToken
    ) {
        final API<TokenResponse> response = tokenService.reIssueAccessToken(accessToken, refreshToken);
        return ResponseEntity.ok(response);
    }

}
