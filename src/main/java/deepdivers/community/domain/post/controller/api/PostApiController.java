package deepdivers.community.domain.post.controller.api;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.controller.docs.PostApiControllerDocs;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.response.PostImageUploadResponse;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
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
	public ResponseEntity<API<PostSaveResponse>> createPost(
		@Auth final Member member,
		@Valid @RequestBody final PostSaveRequest request
	) {
		final API<PostSaveResponse> response = postService.createPost(request, member);
		return ResponseEntity.ok(response);
	}

	@Override
	@PostMapping("/update/{postId}")
	public ResponseEntity<API<PostSaveResponse>> updatePost(
		@Auth final Member member,
		@PathVariable final Long postId,
		@Valid @RequestBody final PostSaveRequest request
	) {
		final API<PostSaveResponse> response = postService.updatePost(postId, request, member);
		return ResponseEntity.ok(response);
	}

	@Override
	@PatchMapping("/delete/{postId}")
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

