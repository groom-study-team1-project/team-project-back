package deepdivers.community.domain.image.application;

import static org.assertj.core.api.Assertions.assertThat;

import deepdivers.community.domain.IntegrationTest;
import deepdivers.community.domain.image.repository.entity.Image;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ImageServiceTest extends IntegrationTest {

    @Autowired ImageService imageService;

    @BeforeEach
    void setUp() {
        createTestObject("posts/content1.png");
        createTestObject("posts/content2.png");
        createTestObject("posts/content3.png");
    }

    @Test
    void 게시글_콘텐츠_이미지를_생성할_수_있다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 2L;

        // when
        imageService.createPostContentImage(imageKeys, postId);

        // then
        List<String> savedImageKeys = getPostContentImages(postId).stream().map(Image::getImageKey).toList();
        assertThat(savedImageKeys).isEqualTo(imageKeys);
    }

    @Test
    void 게시글_콘텐츠_이미지_생성_시_접근_주소가_저장된다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 2L;

        // when
        imageService.createPostContentImage(imageKeys, postId);

        // then
        List<String> accessUrls = imageKeys.stream().map(this::generateAccessUrl).toList();
        List<String> savedImageUrls = getPostContentImages(postId).stream().map(Image::getImageUrl).toList();
        assertThat(savedImageUrls).isEqualTo(accessUrls);
    }

    @Test
    void 게시글_콘텐츠_이미지_생성_시_S3에_저장된_삭제_태그가_사라진다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 1L;

        // when
        imageService.createPostContentImage(imageKeys, postId);

        // then
        assertThat(getTag(imageKeys.getFirst())).isEmpty();
        assertThat(getTag(imageKeys.getLast())).isEmpty();
    }

    @Test
    void 게시글_이미지_수정을_할_수_있다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 2L;
        imageService.createPostContentImage(imageKeys, postId);

        List<String> newImageKeys = List.of("posts/content1.png", "posts/content3.png");

        // when
        imageService.updatePostContentImage(newImageKeys, postId);

        // then
        List<String> savedImageKeys = getPostContentImages(postId).stream().map(Image::getImageKey).toList();
        assertThat(savedImageKeys).isEqualTo(newImageKeys);
    }

    @Test
    void 게시글_이미지_수정_시_사용되지_않는_이미지_키는_삭제_태그가_달린다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 2L;
        imageService.createPostContentImage(imageKeys, postId);

        List<String> newImageKeys = List.of("posts/content1.png", "posts/content3.png");

        // when
        imageService.updatePostContentImage(newImageKeys, postId);

        // then
        assertThat(getTag("posts/content2.png").getFirst().key()).isEqualTo("Status");
        assertThat(getTag("posts/content2.png").getFirst().value()).isEqualTo("Deleted");
    }

}