package deepdivers.community.domain.file.application;

import static org.assertj.core.api.Assertions.assertThat;

import deepdivers.community.domain.IntegrationTest;
import deepdivers.community.domain.file.repository.entity.File;
import deepdivers.community.domain.file.repository.entity.FileType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.s3.model.Tag;

class FileServiceTest extends IntegrationTest {

    @Autowired
    FileService fileService;

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
        fileService.createPostImage(imageKeys, postId, FileType.POST_CONTENT);

        // then
        List<String> savedImageKeys = getPostImages(postId, FileType.POST_CONTENT).stream().map(File::getFileKey).toList();
        assertThat(savedImageKeys).isEqualTo(imageKeys);
    }

    @Test
    void 게시글_콘텐츠_이미지_생성_시_접근_주소가_저장된다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 2L;

        // when
        fileService.createPostImage(imageKeys, postId, FileType.POST_CONTENT);

        // then
        List<String> accessUrls = imageKeys.stream().map(this::generateAccessUrl).toList();
        List<String> savedImageUrls = getPostImages(postId, FileType.POST_CONTENT).stream().map(File::getFileUrl).toList();
        assertThat(savedImageUrls).isEqualTo(accessUrls);
    }

    @Test
    void 게시글_콘텐츠_이미지_생성_시_S3에_저장된_삭제_태그가_사라진다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 1L;

        // when
        fileService.createPostImage(imageKeys, postId, FileType.POST_CONTENT);

        // then
        assertThat(getTag(imageKeys.getFirst())).isEmpty();
        assertThat(getTag(imageKeys.getLast())).isEmpty();
    }

    @Test
    void 게시글_이미지_수정을_할_수_있다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 2L;
        fileService.createPostImage(imageKeys, postId, FileType.POST_CONTENT);

        List<String> newImageKeys = List.of("posts/content1.png", "posts/content3.png");

        // when
        fileService.updatePostImage(newImageKeys, postId, FileType.POST_CONTENT);

        // then
        List<String> savedImageKeys = getPostImages(postId,FileType.POST_CONTENT).stream().map(File::getFileKey).toList();
        assertThat(savedImageKeys).isEqualTo(newImageKeys);
    }

    @Test
    void 게시글_이미지_수정_시_사용되지_않는_이미지_키는_삭제_태그가_달린다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 2L;
        fileService.createPostImage(imageKeys, postId, FileType.POST_CONTENT);

        List<String> newImageKeys = List.of("posts/content1.png", "posts/content3.png");

        // when
        fileService.updatePostImage(newImageKeys, postId, FileType.POST_CONTENT);

        // then
        Tag tag = Tag.builder().key("Status").value("Deleted").build();
        assertThat(getTag("posts/content2.png").getFirst()).isEqualTo(tag);
    }

    @Test
    void 게시글_이미지가_S3에서_제거된다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 2L;
        fileService.createPostImage(imageKeys, postId, FileType.POST_CONTENT);

        // when
        fileService.deletePostImage(postId, FileType.POST_CONTENT);

        // then
        Tag tag = Tag.builder().key("Status").value("Deleted").build();
        assertThat(getTag("posts/content1.png").getFirst()).isEqualTo(tag);
        assertThat(getTag("posts/content2.png").getFirst()).isEqualTo(tag);
    }

    @Test
    void 게시글_이미지가_DB에서_제거된다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 2L;
        fileService.createPostImage(imageKeys, postId, FileType.POST_CONTENT);

        // when
        fileService.deletePostImage(postId, FileType.POST_CONTENT);

        // then
        List<File> postImages = getPostImages(postId, FileType.POST_CONTENT);
        assertThat(postImages).isEmpty();
    }

    @Test
    void 슬라이드_이미지_또한_잘된다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 2L;
        fileService.createPostImage(imageKeys, postId, FileType.POST_CONTENT);

        // when
        fileService.deletePostImage(postId, FileType.POST_CONTENT);

        // then
        List<File> postImages = getPostImages(postId, FileType.POST_CONTENT);
        assertThat(postImages).isEmpty();
    }

    @Test
    void 게시글_슬라이드_이미지를_생성할_수_있다() {
        // given
        List<String> imageKeys = List.of("posts/content1.png", "posts/content2.png");
        Long postId = 2L;

        // when
        fileService.createPostImage(imageKeys, postId, FileType.POST_SLIDE);

        // then
        List<String> savedImageKeys = getPostImages(postId, FileType.POST_SLIDE).stream().map(File::getFileKey).toList();
        assertThat(savedImageKeys).isEqualTo(imageKeys);
    }

}