package deepdivers.community.domain.post.controller;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import deepdivers.community.domain.post.service.ProjectPostService;
import deepdivers.community.global.security.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/projects")
@RestController
@RequiredArgsConstructor
public class ProjectPostApiController {

    private final ProjectPostService projectPostService;

    @PostMapping("/upload")
    public ResponseEntity<API<Long>> createProject(
        @Auth final Member member,
        @Valid @RequestBody final ProjectPostRequest dto
    ) {
        final API<Long> result = projectPostService.createProjectPost(member, dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/edit/{projectId}")
    public ResponseEntity<API<Long>> editProject(
        @Auth final Member member,
        @Valid @RequestBody final ProjectPostRequest dto,
        @PathVariable final Long projectId
    ) {
        final API<Long> result = projectPostService.updateProjectPost(projectId, member, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/remove/{projectId}")
    public ResponseEntity<NoContent> removeProject(
        @Auth final Member member,
        @PathVariable final Long projectId
    ) {
        final NoContent noContent = projectPostService.deletePost(projectId, member);
        return ResponseEntity.ok(noContent);
    }

}
