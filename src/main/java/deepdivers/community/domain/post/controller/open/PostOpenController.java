package deepdivers.community.domain.post.controller.open;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.controller.docs.PostOpenControllerDocs;
import deepdivers.community.domain.post.dto.response.PostAllReadResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open/posts")
public class PostOpenController implements PostOpenControllerDocs {

	private final PostService postService;

	@Override
	@GetMapping("/{postId}")
	public ResponseEntity<API<PostReadResponse>> getPostById(
		@PathVariable Long postId,
		HttpServletRequest request
	) {
		PostReadResponse response = postService.getPostById(postId, request.getRemoteAddr());
		return ResponseEntity.ok(API.of(PostStatusType.POST_VIEW_SUCCESS, response));
	}

	@Override
	@GetMapping
	public ResponseEntity<API<List<PostAllReadResponse>>> getAllPosts(
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) Long lastPostId
	) {

		// Log 추가: 서비스 호출 전에 로그 추가
		System.out.println("PostOpenController - getAllPosts() 호출 시작");

		if (lastPostId == null) {
			lastPostId = Long.MAX_VALUE;  // lastPostId가 없으면 Long.MAX_VALUE 사용
		}
		List<PostAllReadResponse> response = postService.getAllPosts(lastPostId, categoryId);

		System.out.println("PostOpenController - postService에서 반환된 게시글 목록: " + response);

		if (response.isEmpty()) {
			System.out.println("PostOpenController - 빈 목록이 반환되었습니다.");
		}

		return ResponseEntity.ok(API.of(PostStatusType.POST_VIEW_SUCCESS, response));
	}
}
