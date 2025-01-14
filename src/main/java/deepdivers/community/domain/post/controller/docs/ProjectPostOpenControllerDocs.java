package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.ExceptionResponse;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPreviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "06. 프로젝트 게시글 조회", description = "프로젝트 게시글 조회 API")
public interface ProjectPostOpenControllerDocs {

}
