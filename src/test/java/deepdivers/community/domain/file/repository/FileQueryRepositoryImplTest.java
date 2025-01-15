package deepdivers.community.domain.file.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import deepdivers.community.domain.RepositoryTest;
import deepdivers.community.domain.file.repository.entity.FileType;
import java.util.List;
import org.junit.jupiter.api.Test;

class FileQueryRepositoryImplTest extends RepositoryTest {

    @Test
    void findAllImageUrlsByPostContent() {
        // given, test.sql
        Long postId = 1L;

        // when
        List<String> result = fileQueryRepository.findAllImageUrlsByPost(postId, FileType.POST_CONTENT);

        // then
        assertThat(result.getFirst()).contains("thumbnail.png");
    }

    @Test
    void findAllImageUrlsByPostSide() {
        // given, test.sql
        Long postId = 1L;

        // when
        List<String> result = fileQueryRepository.findAllImageUrlsByPost(postId, FileType.POST_SLIDE);

        // then
        assertThat(result).hasSize(0);

    }

}