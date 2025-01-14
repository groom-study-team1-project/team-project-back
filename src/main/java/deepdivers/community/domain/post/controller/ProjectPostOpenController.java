package deepdivers.community.domain.post.controller;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.controller.docs.ProjectPostApiControllerDocs;
import deepdivers.community.domain.post.controller.docs.ProjectPostOpenControllerDocs;
import deepdivers.community.domain.post.controller.interfaces.ProjectPostQueryRepository;
import deepdivers.community.domain.post.dto.code.PostStatusCode;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPreviewResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open/projects")
public class ProjectPostOpenController implements ProjectPostOpenControllerDocs {

    private final ProjectPostQueryRepository pqRepository;

    @Override
    public ResponseEntity<API<PostDetailResponse>> getPostById(Long postId, Long viewerId) {
        return null;
    }

    @GetMapping
    public ResponseEntity<API<List<ProjectPostPreviewResponse>>> getAllPosts(GetPostsRequest request) {
        return ResponseEntity.ok(API.of(
            PostStatusCode.PROJECT_POST_DELETE_SUCCESS,
            pqRepository.findAllPosts(null, request)
        ));
    }

    @Override
    public ResponseEntity<API<List<PostPreviewResponse>>> getMyAllPosts(Long memberId, GetPostsRequest dto) {
        return null;
    }
}
