package deepdivers.community.domain.post.controller;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.post.aspect.IncreaseViewCount;
import deepdivers.community.domain.post.controller.docs.ProjectPostOpenControllerDocs;
import deepdivers.community.domain.post.controller.interfaces.ProjectPostQueryRepository;
import deepdivers.community.domain.post.dto.code.PostStatusCode;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.ProjectPostDetailResponse;
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
@RequestMapping("/open/projects")
public class ProjectPostOpenController implements ProjectPostOpenControllerDocs {

    private final ProjectPostQueryRepository pqRepository;

    @GetMapping("/{postId}")
    @IncreaseViewCount
    public ResponseEntity<API<ProjectPostDetailResponse>> getPostById(
        @PathVariable final Long postId,
        @Auth final Long viewerId
    ) {
        return ResponseEntity.ok(API.of(
            PostStatusCode.POST_VIEW_SUCCESS,
            pqRepository.readPostByPostId(postId, viewerId))
        );
    }

    @GetMapping
    public ResponseEntity<API<List<ProjectPostPreviewResponse>>> getAllPosts(
        @ModelAttribute final GetPostsRequest request
    ) {
        return ResponseEntity.ok(API.of(
            PostStatusCode.POST_VIEW_SUCCESS,
            pqRepository.findAllPosts(null, request)
        ));
    }

    @GetMapping("/me/{memberId}")
    public ResponseEntity<API<List<ProjectPostPreviewResponse>>> getMyAllPosts(
        @PathVariable final Long memberId,
        @ModelAttribute final GetPostsRequest dto
    ) {
        return ResponseEntity.ok(API.of(
            PostStatusCode.POST_VIEW_SUCCESS,
            pqRepository.findAllPosts(memberId, dto)
        ));
    }

}
