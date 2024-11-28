package deepdivers.community.domain.post.controller.open;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.controller.docs.PostOpenControllerDocs;
import deepdivers.community.domain.post.dto.response.GetAllPostsResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.repository.PostQueryRepository;
import deepdivers.community.domain.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
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
	public ResponseEntity<API<List<GetAllPostsResponse>>> getAllPosts(
		@RequestParam(required = false) final Long categoryId,
		@RequestParam(required = false) final Long lastPostId
	) {
		return ResponseEntity.ok(API.of(
			PostStatusType.POST_VIEW_SUCCESS,
			postQueryRepository.findAllPosts(lastPostId, categoryId)
		));
	}

}
