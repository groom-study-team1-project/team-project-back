package deepdivers.community.domain.post.controller.api;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.controller.docs.PostApiControllerDocs;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.request.PostUpdateRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.PostUpdateResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.domain.global.security.jwt.Auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
		@PathVariable Long postId,
		HttpServletRequest request
	) {
		String ipAddr = request.getRemoteAddr();
		PostReadResponse response = postService.getPostById(postId, ipAddr);
		return ResponseEntity.ok(API.of(PostStatusType.POST_VIEW_SUCCESS, response));
	}

	@GetMapping
	public ResponseEntity<API<List<PostReadResponse>>> getAllPosts(
		@Auth final Member member
	) {
		List<PostReadResponse> response = postService.getAllPosts();
		return ResponseEntity.ok(API.of(PostStatusType.POST_VIEW_SUCCESS, response));
	}

	@PostMapping("/update/{postId}")
	public ResponseEntity<API<PostUpdateResponse>> updatePost(
		@Auth final Member member,
		@PathVariable final Long postId,
		@Valid @RequestBody final PostUpdateRequest request
	) {
		final API<PostUpdateResponse> response = postService.updatePost(postId, request, member);
		return ResponseEntity.ok(response);  // 변경된 결과 구조로 반환
	}
}
