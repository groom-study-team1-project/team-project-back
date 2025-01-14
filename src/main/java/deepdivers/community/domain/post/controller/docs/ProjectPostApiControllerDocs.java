package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

@Schema(description = "06. 프로젝트 게시글")
public interface ProjectPostApiControllerDocs {

}
