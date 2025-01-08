package deepdivers.community.domain.image.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import deepdivers.community.domain.RepositoryTest;
import java.util.List;
import org.junit.jupiter.api.Test;

class ImageQueryRepositoryImplTest extends RepositoryTest {

    @Test
    void findAllImageUrlsByPost() {
        // given, test.sql
        Long postId = 1L;

        // when
        List<String> result = imageQueryRepository.findAllImageUrlsByPost(postId);

        // then
        assertThat(result.getFirst()).contains("thumbnail.png");
    }

}