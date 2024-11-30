package deepdivers.community.infra.aws.s3.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Getter
@Configuration
public class S3Properties {

    private final String bucket;
    private final String baseUrl;

    public S3Properties(
        @Value("${spring.cloud.aws.s3.bucket}") String bucket,
        @Value("${spring.cloud.aws.s3.endpoint:}") String endpoint,
        @Value("${spring.cloud.aws.region.static}") String region
    ) {
        this.bucket = bucket;
        this.baseUrl = generateBaseUrl(bucket, endpoint, region);
    }

    private String generateBaseUrl(final String bucket, final String endpoint, final String region) {
        if (StringUtils.hasText(endpoint)) {
            return endpoint;
        }

        return String.format("https://%s.s3.%s.amazonaws.com", bucket, region);
    }

}