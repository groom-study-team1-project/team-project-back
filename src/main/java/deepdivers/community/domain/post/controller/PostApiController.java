package deepdivers.community.domain.post.controller;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.controller.docs.PostApiControllerDocs;
import deepdivers.community.domain.like.dto.LikeRequest;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.like.service.LikeService;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.global.security.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostApiController implements PostApiControllerDocs {

	private final PostService postService;

	@Override
	@PostMapping("/upload")
	public ResponseEntity<API<PostSaveResponse>> createPost(
		@Auth final Member member,
		@Valid @RequestBody final PostSaveRequest request
	) {
		final API<PostSaveResponse> response = postService.createPost(request, member);
		return ResponseEntity.ok(response);
	}

	@Override
	@PostMapping("/edit/{postId}")
	public ResponseEntity<API<PostSaveResponse>> updatePost(
		@Auth final Member member,
		@PathVariable final Long postId,
		@Valid @RequestBody final PostSaveRequest request
	) {
		final API<PostSaveResponse> response = postService.updatePost(postId, request, member);
		return ResponseEntity.ok(response);
	}

	@Override
	@PatchMapping("/remove/{postId}")
	public ResponseEntity<NoContent> deletePost(
		@Auth final Member member,
		@PathVariable final Long postId
	) {
		final NoContent response = postService.deletePost(postId, member);
		return ResponseEntity.ok(response);
	}

}

