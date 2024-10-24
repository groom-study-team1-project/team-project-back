package deepdivers.community.domain.post.controller.api;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.controller.docs.PostApiControllerDocs;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.request.PostUpdateRequest;
import deepdivers.community.domain.post.dto.response.*;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.global.security.jwt.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
	@GetMapping("/{postId}")
	public ResponseEntity<API<PostReadResponse>> getPostById(
		@Auth Member member,
		@PathVariable Long postId
	) {
		PostReadResponse response = postService.getPostById(postId, "");
		return ResponseEntity.ok(API.of(PostStatusType.POST_VIEW_SUCCESS, response));
	}

	@GetMapping
	public ResponseEntity<API<PostCountResponse>> getAllPosts(
		@Auth final Member member,
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) Long lastPostId
	) {
		if (lastPostId == null) {
			lastPostId = Long.MAX_VALUE;  // lastPostId가 없으면 Long.MAX_VALUE 사용
		}
		API<PostCountResponse> response = postService.getAllPosts(lastPostId, categoryId);
		return ResponseEntity.ok(response);
	}

	@Override
	@PostMapping("/update/{postId}")
	public ResponseEntity<API<PostUpdateResponse>> updatePost(
		@Auth final Member member,
		@PathVariable final Long postId,
		@Valid @RequestBody final PostUpdateRequest request
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

	@PostMapping(value = "/upload/image", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<API<PostImageUploadResponse>> postImageUpload(
			@RequestParam final MultipartFile imageFile
	) {
		API<PostImageUploadResponse> response = postService.postImageUpload(imageFile);
		return ResponseEntity.ok(response);
	}
}

