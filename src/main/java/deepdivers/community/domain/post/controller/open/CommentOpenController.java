package deepdivers.community.domain.post.controller.open;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.dto.response.CommentResponse;
import deepdivers.community.domain.post.dto.response.statustype.CommentStatusType;
import deepdivers.community.domain.post.repository.CommentQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentOpenController {

    private final CommentQueryRepository commentQueryRepository;

    @GetMapping("/{postId}")
    public ResponseEntity<API<List<CommentResponse>>> getCommentsOnPost(
        @PathVariable final Long postId,
        @RequestParam(required = false, defaultValue = "0") final Long memberId,
        @RequestParam(required = false, defaultValue = "9223372036854775807") final Long lastCommentId
    ) {
        final List<CommentResponse> response =
            commentQueryRepository.findTop5CommentsByPost(postId, memberId, lastCommentId);
        return ResponseEntity.ok(API.of(CommentStatusType.COMMENT_REMOVE_SUCCESS, response));
    }

}
