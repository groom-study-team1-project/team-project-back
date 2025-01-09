package deepdivers.community.domain.comment.controller;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.comment.controller.docs.CommentOpenControllerDocs;
import deepdivers.community.domain.comment.dto.response.ContentResponse;
import deepdivers.community.domain.comment.dto.response.GetCommentResponse;
import deepdivers.community.domain.comment.dto.code.CommentStatusCode;
import deepdivers.community.domain.comment.controller.interfaces.CommentQueryRepository;
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
@RequestMapping("/open/comments")
public class CommentOpenController implements CommentOpenControllerDocs {

    private final CommentQueryRepository commentQueryRepository;

    @GetMapping("/{postId}")
    public ResponseEntity<API<List<GetCommentResponse>>> getCommentsOnPost(
        @PathVariable final Long postId,
        @RequestParam(required = false, defaultValue = "0") final Long memberId,
        @RequestParam(required = false, defaultValue = "9223372036854775807") final Long lastCommentId
    ) {
        final List<GetCommentResponse> response =
            commentQueryRepository.findTop5CommentsByPost(postId, memberId, lastCommentId);
        return ResponseEntity.ok(API.of(CommentStatusCode.COMMENT_GET_SUCCESS, response));
    }

    @GetMapping("/replies/{commentId}")
    public ResponseEntity<API<List<ContentResponse>>> getRepliesOnComment(
        @PathVariable final Long commentId,
        @RequestParam(required = false, defaultValue = "0") final Long memberId,
        @RequestParam(required = false, defaultValue = "9223372036854775807") final Long lastCommentId
    ) {
        final List<ContentResponse> response =
            commentQueryRepository.findTop5RepliesByComment(commentId, memberId, lastCommentId);
        return ResponseEntity.ok(API.of(CommentStatusCode.REPLY_GET_SUCCESS, response));
    }

}
