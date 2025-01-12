package deepdivers.community.domain.like.controller;

import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.like.controller.docs.LikeControllerDocs;
import deepdivers.community.domain.like.dto.LikeRequest;
import deepdivers.community.domain.like.service.LikeService;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.service.PostService;
import deepdivers.community.global.security.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController implements LikeControllerDocs {

	private final LikeService likeService;

	@PostMapping("/posts")
	public ResponseEntity<NoContent> likePost(
			@Auth final Member member,
			@RequestBody final LikeRequest request
	) {
		return ResponseEntity.ok(likeService.likePost(request, member.getId()));
	}

	@DeleteMapping("/posts")
	public ResponseEntity<NoContent> unlikePost(
			@Auth final Member member,
			@RequestBody final LikeRequest request
	) {
		return ResponseEntity.ok(likeService.unlikePost(request, member.getId()));
	}

	@PostMapping("/comments")
	public ResponseEntity<NoContent> likeComment(
		@Auth final Member member,
		@Valid @RequestBody final LikeRequest request
	) {
		return ResponseEntity.ok(likeService.likeComment(request, member.getId()));
	}

	@DeleteMapping("/comments")
	public ResponseEntity<NoContent> unlikeComment(
		@Auth final Member member,
		@Valid @RequestBody final LikeRequest request
	) {
		return ResponseEntity.ok(likeService.unlikeComment(request, member.getId()));
	}

}

