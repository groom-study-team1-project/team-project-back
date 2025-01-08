package deepdivers.community.domain.image.ui;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.image.application.dto.request.GetPresignRequest;
import deepdivers.community.domain.image.application.dto.response.GetPresignResponse;
import deepdivers.community.domain.image.application.dto.response.statustype.UploaderStatusType;
import deepdivers.community.domain.image.ui.docs.UploaderOpenControllerDocs;
import deepdivers.community.infra.aws.s3.S3PresignManager;
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

    private final S3PresignManager s3PresignManager;

    @PostMapping("/presigned-url-generation")
    public ResponseEntity<API<GetPresignResponse>> generatePresignUrl(
        @Valid @RequestBody
        final GetPresignRequest request
    ) {
        final String key = s3PresignManager.generateKey(request.contentType(), request.keyType());
        final String presignedUrl = s3PresignManager.generatePreSignedUrl(key, request.contentType());
        final String accessUrl = s3PresignManager.generateAccessUrl(key);

        return ResponseEntity.ok(API.of(
            UploaderStatusType.GENERATE_PRESIGN_SUCCESS,
            GetPresignResponse.of(key, presignedUrl, accessUrl)
        ));
    }

}
