package deepdivers.community.domain.post.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import deepdivers.community.domain.common.API;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.controller.docs.PostApiControllerDocs;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.global.security.jwt.Auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
}
