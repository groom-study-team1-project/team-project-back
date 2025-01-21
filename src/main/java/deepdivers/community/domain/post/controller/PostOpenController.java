package deepdivers.community.domain.post.controller;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.post.aspect.IncreaseViewCount;
import deepdivers.community.domain.post.controller.docs.PostOpenControllerDocs;
import deepdivers.community.domain.post.controller.interfaces.ProjectPostQueryRepository;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.NormalPostPageResponse;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.dto.code.PostStatusCode;
import deepdivers.community.domain.post.controller.interfaces.PostQueryRepository;
import deepdivers.community.domain.post.dto.response.ProjectPostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPageResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPreviewResponse;
import deepdivers.community.global.security.Auth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open/posts")
public class PostOpenController implements PostOpenControllerDocs {

	private final PostQueryRepository postQueryRepository;
	private final ProjectPostQueryRepository pqRepository;

	@GetMapping("/{postId}")
	@IncreaseViewCount
	public ResponseEntity<API<PostDetailResponse>> getPostById(
		@PathVariable final Long postId,
		@Auth final Long viewerId
	) {
		final PostDetailResponse postDetailResponse = postQueryRepository.readPostByPostId(postId, viewerId);
		return ResponseEntity.ok(API.of(PostStatusCode.POST_VIEW_SUCCESS, postDetailResponse));
	}

	@GetMapping
	public ResponseEntity<API<List<PostPreviewResponse>>> getAllPosts(@ModelAttribute final GetPostsRequest dto) {
		return ResponseEntity.ok(API.of(
			PostStatusCode.POST_VIEW_SUCCESS,
			postQueryRepository.findAllPosts(null, dto)
		));
	}

	@GetMapping("/page")
	public ResponseEntity<API<NormalPostPageResponse>> getNormalPostApi(@ModelAttribute final GetPostsRequest dto) {
		return ResponseEntity.ok(API.of(
			PostStatusCode.POST_VIEW_SUCCESS,
			postQueryRepository.getNormalPostPageQuery(dto)
		));
	}

	@GetMapping("/me/{memberId}")
	public ResponseEntity<API<List<PostPreviewResponse>>> getAllPostsByMember(
		@PathVariable final Long memberId,
		@ModelAttribute final GetPostsRequest dto
	) {
		return ResponseEntity.ok(API.of(
			PostStatusCode.MY_POSTS_GETTING_SUCCESS,
			postQueryRepository.findAllPosts(memberId, dto)
		));
	}

	@GetMapping("/project/{postId}")
	@IncreaseViewCount
	public ResponseEntity<API<ProjectPostDetailResponse>> getProjectPostById(
		@PathVariable final Long postId,
		@Auth final Long viewerId
	) {
		return ResponseEntity.ok(API.of(
			PostStatusCode.POST_VIEW_SUCCESS,
			pqRepository.readPostByPostId(postId, viewerId))
		);
	}

	@GetMapping("/project")
	public ResponseEntity<API<List<ProjectPostPreviewResponse>>> getAllProjectPosts(
		@ModelAttribute final GetPostsRequest request
	) {
		return ResponseEntity.ok(API.of(
			PostStatusCode.POST_VIEW_SUCCESS,
			pqRepository.findAllPosts(null, request)
		));
	}

	@GetMapping("/project/me/{memberId}")
	public ResponseEntity<API<List<ProjectPostPreviewResponse>>> getAllProjectPostsByMember(
		@PathVariable final Long memberId,
		@ModelAttribute final GetPostsRequest dto
	) {
		return ResponseEntity.ok(API.of(
			PostStatusCode.POST_VIEW_SUCCESS,
			pqRepository.findAllPosts(memberId, dto)
		));
	}

	@GetMapping("/project/page")
	public ResponseEntity<API<ProjectPostPageResponse>> getProjectPostApi(
		@ModelAttribute final GetPostsRequest request
	) {
		return ResponseEntity.ok(API.of(
			PostStatusCode.POST_VIEW_SUCCESS,
			pqRepository.generateNormalPostPageQuery(request)
		));
	}

}
