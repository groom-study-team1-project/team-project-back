package deepdivers.community.domain.uploader.application;

import static deepdivers.community.domain.uploader.application.dto.response.statustype.UploaderStatusType.GENERATE_PRESIGN_SUCCESS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.uploader.application.dto.request.GetPresignRequest;
import deepdivers.community.domain.uploader.application.dto.response.GetPresignResponse;
import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.infra.aws.s3.KeyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

@Import(LocalStackTestConfig.class)
@SpringBootTest
@DirtiesContext
class UploaderServiceTest {

    @Autowired private UploaderService uploaderService;

    @Test
    @DisplayName("올바른 요청 시 presignUrl 생성이 된다.")
    void givenPresignRequestWhenGeneratePresignUrlThenReturnPresignResponse() {
        // given
        GetPresignRequest request = new GetPresignRequest("image/png", KeyType.POST);

        // when
        API<GetPresignResponse> response = uploaderService.generatePresignUrl(request);

        // then
        assertThat(response.status().code()).isEqualTo(GENERATE_PRESIGN_SUCCESS.getCode());
        assertThat(response.status().message()).isEqualTo(GENERATE_PRESIGN_SUCCESS.getMessage());
        assertThat(response.getResult().fileKey()).startsWith("posts/");
        assertThat(response.getResult().fileKey()).endsWith(".png");
        assertThat(response.getResult().presignedUrl()).startsWith("https://test-bucket");
        assertThat(response.getResult().accessUrl()).startsWith("http://localhost:4566/posts/");
    }

}