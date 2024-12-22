package deepdivers.community.infra.aws.s3.generator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class S3KeyGeneratorTest {

    @ParameterizedTest
    @CsvSource({
        "image/jpeg, .jpeg",
        "image/png, .png",
        "image/gif, .gif"
    })
    @DisplayName("contentType의 확장자로 끝나는 key가 생성된다.")
    void givenContentType_whenGenerateKey_thenEndsWithContentExtension(String contentType, String extension) {
        // given
        // when
        String key = S3KeyGenerator.generateProfileKey(contentType, "uuid");

        // then
        assertThat(key).endsWith(extension);
    }

    @Test
    @DisplayName("프로필 키 생성 시 'profiles/'로 시작한다.")
    void whenGenerateProfileKeyThenStartWithProfiles() {
        // given
        // when
        String key = S3KeyGenerator.generateProfileKey("image/png", "uuid");

        // then
        assertThat(key).startsWith("profiles/");
    }

    @Test
    @DisplayName("포스트 키 생성 시 'posts/'로 시작한다.")
    void whenGeneratePostKeyThenStartWithPosts() {
        // given
        // when
        String key = S3KeyGenerator.generatePostKey("image/png", "uuid");

        // then
        assertThat(key).startsWith("posts/");
    }

    @Test
    @DisplayName("uuid가 주어질 경우, key에 uuid가 포함된다.")
    void givenUUID_whenGenerateKeyThenContainsUUID() {
        // given
        String uuid = "uuid";

        // when
        String key = S3KeyGenerator.generatePostKey("image/png", uuid);

        // then
        assertThat(key).contains(uuid);
    }

}