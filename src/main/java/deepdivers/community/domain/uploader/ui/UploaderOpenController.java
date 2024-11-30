package deepdivers.community.domain.uploader.ui;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.uploader.application.UploaderService;
import deepdivers.community.domain.uploader.application.dto.request.GetPresignRequest;
import deepdivers.community.domain.uploader.application.dto.response.GetPresignResponse;
import deepdivers.community.domain.uploader.ui.docs.UploaderOpenControllerDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/open/uploader")
public class UploaderOpenController implements UploaderOpenControllerDocs {

    private final UploaderService uploaderService;

    @PostMapping("/presigned-url-generation")
    public ResponseEntity<API<GetPresignResponse>> generatePresignUrl(
        @Valid @RequestBody
        final GetPresignRequest request
    ) {
        return ResponseEntity.ok(
            uploaderService.generatePresignUrl(request)
        );
    }

}
