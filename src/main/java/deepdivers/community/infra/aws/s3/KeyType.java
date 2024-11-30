package deepdivers.community.infra.aws.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KeyType {

    PROFILE("profiles/"),
    POST("posts/");

    private final String prefix;

}
