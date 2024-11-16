package deepdivers.community.domain.post.controller.api;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.controller.docs.PostApiControllerDocs;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.PostUpdateResponse;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.global.security.jwt.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostApiController implements PostApiControllerDocs {

	private final PostService postService;

	@Override
	@PostMapping("/upload")
	public ResponseEntity<API<PostCreateResponse>> createPost(
		@Auth final Member member,
		@Valid @RequestBody final PostCreateRequest request
	) {
		final API<PostCreateResponse> response = postService.createPost(request, member);
		return ResponseEntity.ok(response);
	}

	@Override
	@PostMapping("/update/{postId}")
	public ResponseEntity<API<PostUpdateResponse>> updatePost(
		@Auth final Member member,
		@PathVariable final Long postId,
		@Valid @RequestBody final PostCreateRequest request
	) {
		final API<PostUpdateResponse> response = postService.updatePost(postId, request, member);
		return ResponseEntity.ok(response);
	}

	@Override
	@DeleteMapping("/delete/{postId}")
	public ResponseEntity<NoContent> deletePost(
		@Auth final Member member,
		@PathVariable final Long postId
	) {
		final NoContent response = postService.deletePost(postId, member);
		return ResponseEntity.ok(response);
	}
}

