package deepdivers.community.domain.uploader.application;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.uploader.application.dto.request.GetPresignRequest;
import deepdivers.community.domain.uploader.application.dto.response.GetPresignResponse;
import deepdivers.community.domain.uploader.application.dto.response.statustype.UploaderStatusType;
import deepdivers.community.infra.aws.s3.S3ObjectInspector;
import deepdivers.community.infra.aws.s3.S3PresignManager;
import deepdivers.community.infra.aws.s3.S3TagManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploaderService {

    private final S3PresignManager s3PresignManager;
    private final S3TagManager s3TagManager;
    private final S3ObjectInspector s3ObjectInspector;

    public API<GetPresignResponse> generatePresignUrl(final GetPresignRequest request) {
        final String key = s3PresignManager.generateKey(request.contentType(), request.keyType());
        final String presignedUrl = s3PresignManager.generatePreSignedUrl(key, request.contentType());
        final String accessUrl = s3PresignManager.generateAccessUrl(key);

        return API.of(
            UploaderStatusType.GENERATE_PRESIGN_SUCCESS,
            GetPresignResponse.of(key, presignedUrl, accessUrl)
        );
    }

}
