package deepdivers.community.domain.post.controller.open;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.controller.docs.PostOpenControllerDocs;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.repository.PostQueryRepository;
import deepdivers.community.global.security.Auth;
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

	@Override
	@GetMapping("/{postId}")
	public ResponseEntity<API<PostDetailResponse>> getPostById(
		@PathVariable final Long postId,
		@Auth final Long viewerId
	) {
		final PostDetailResponse postDetailResponse = postQueryRepository.readPostByPostId(postId, viewerId);
		return ResponseEntity.ok(API.of(PostStatusType.POST_VIEW_SUCCESS, postDetailResponse));
	}

	@GetMapping
	public ResponseEntity<API<List<PostPreviewResponse>>> getAllPosts(
		@RequestParam(required = false) final Long categoryId,
		@RequestParam(required = false) final Long lastPostId
	) {
		return ResponseEntity.ok(API.of(
			PostStatusType.POST_VIEW_SUCCESS,
			postQueryRepository.findAllPosts(null, lastPostId, categoryId)
		));
	}

	@GetMapping("/me/{memberId}")
	public ResponseEntity<API<List<PostPreviewResponse>>> getMyAllPosts(
		@PathVariable final Long memberId,
		@RequestParam(required = false) final Long categoryId,
		@RequestParam(required = false) final Long lastPostId
	) {
		return ResponseEntity.ok(API.of(
			PostStatusType.MY_POSTS_GETTING_SUCCESS,
			postQueryRepository.findAllPosts(memberId, lastPostId, categoryId)
		));
	}

}
