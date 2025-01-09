package deepdivers.community.domain.comment.controller;

import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.comment.controller.docs.CommentApiControllerDocs;
import deepdivers.community.domain.comment.dto.request.EditCommentRequest;
import deepdivers.community.domain.like.dto.LikeRequest;
import deepdivers.community.domain.comment.dto.request.RemoveCommentRequest;
import deepdivers.community.domain.comment.dto.request.WriteCommentRequest;
import deepdivers.community.domain.comment.dto.request.WriteReplyRequest;
import deepdivers.community.domain.comment.service.CommentService;
import deepdivers.community.domain.like.service.LikeService;
import deepdivers.community.global.security.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentApiController implements CommentApiControllerDocs {

    private final CommentService commentService;
    private final LikeService likeService;

    @PostMapping("/write")
    public ResponseEntity<NoContent> writeCommentOnPosts(
        @Auth final Member member,
        @RequestBody @Valid final WriteCommentRequest request
    ) {
        final NoContent response = commentService.writeComment(member, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/write/reply")
    public ResponseEntity<NoContent> writeReplyOnComment(
        @Auth final Member member,
        @RequestBody @Valid final WriteReplyRequest request
    ) {
        final NoContent response = commentService.writeReply(member, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<NoContent> removeCommentOnPost(
        @Auth final Member member,
        @RequestBody @Valid final RemoveCommentRequest request
    ) {
        final NoContent response = commentService.removeComment(member, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/edit")
    public ResponseEntity<NoContent> updateComment(
        @Auth final Member member,
        @RequestBody @Valid final EditCommentRequest request
    ) {
        final NoContent response = commentService.updateComment(member, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/like")
    public ResponseEntity<NoContent> likeComment(
        @Auth final Member member,
        @Valid @RequestBody final LikeRequest request
    ) {
        final NoContent response = likeService.likeComment(request, member.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unlike")
    public ResponseEntity<NoContent> unlikeComment(
        @Auth final Member member,
        @Valid @RequestBody final LikeRequest request
    ) {
        final NoContent response = likeService.unlikeComment(request, member.getId());
        return ResponseEntity.ok(response);
    }

}
