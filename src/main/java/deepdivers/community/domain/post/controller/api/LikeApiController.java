package deepdivers.community.domain.post.controller.api;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.controller.docs.LikeControllerDocs;
import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.service.LikeService;
import deepdivers.community.global.security.jwt.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeApiController implements LikeControllerDocs {

    private final LikeService likeService;

    @PostMapping("/comments")
    public ResponseEntity<NoContent> likeComment(
        @Auth final Member member,
        @RequestBody final LikeRequest request
    ) {
        final NoContent response = likeService.likeComment(request, member.getId());
        return ResponseEntity.ok(response);
    }

}
