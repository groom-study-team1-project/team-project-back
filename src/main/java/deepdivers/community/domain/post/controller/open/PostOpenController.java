package deepdivers.community.domain.post.controller.open;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.controller.docs.PostOpenControllerDocs;
import deepdivers.community.domain.post.dto.response.PostAllReadResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.repository.PostQueryRepository;
import deepdivers.community.domain.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open/posts")
public class PostOpenController implements PostOpenControllerDocs {

	private final PostQueryRepository postQueryRepository;
	private final PostService postService;

	@Override
	@GetMapping("/{postId}")
	public ResponseEntity<API<PostReadResponse>> getPostById(
		@PathVariable final Long postId,
		final HttpServletRequest request
	) {
		final PostReadResponse response = postService.readPostDetail(postId, request.getRemoteAddr());
		return ResponseEntity.ok(API.of(PostStatusType.POST_VIEW_SUCCESS, response));
	}

	@GetMapping
	public ResponseEntity<API<List<PostAllReadResponse>>> getAllPosts(
		@RequestParam(required = false) final Long categoryId,
		@RequestParam(required = false) final Long lastPostId
	) {
		return ResponseEntity.ok(API.of(
				PostStatusType.POST_VIEW_SUCCESS,
				postQueryRepository.findAllPosts(lastPostId, categoryId)
		));
	}
}
