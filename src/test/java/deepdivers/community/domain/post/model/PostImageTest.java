package deepdivers.community.domain.post.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostImageTest {

    @Test
    @DisplayName("PostImage 객체가 올바르게 생성되는 것을 확인한다.")
    void createPostImageSuccessfully() {
        // given
        Post post = new Post();
        String imageUrl = "http://example.com/image.jpg";

        // when
        PostImage postImage = new PostImage(post, imageUrl);

        // then
        assertThat(postImage).isNotNull();
        assertThat(postImage.getPost()).isEqualTo(post);
        assertThat(postImage.getImageKey()).isEqualTo(imageUrl);
    }

    @Test
    @DisplayName("PostImage 객체의 동등성을 확인한다.")
    void equalPostImagesShouldBeEqual() {
        // given
        Post post = new Post();
        String imageUrl = "http://example.com/image.jpg";

        PostImage basePostImage = new PostImage(post, imageUrl);
        PostImage comparePostImage = new PostImage(post, imageUrl);

        // then
        assertThat(basePostImage).isEqualTo(comparePostImage);
    }

    @Test
    @DisplayName("PostImage 객체의 URL이 다르면 동등하지 않다.")
    void postImagesWithDifferentUrlsShouldNotBeEqual() {
        // given
        Post post = new Post();

        PostImage basePostImage = new PostImage(post, "http://example.com/image1.jpg");
        PostImage comparePostImage = new PostImage(post, "http://example.com/image2.jpg");

        // then
        assertThat(basePostImage).isNotEqualTo(comparePostImage);
    }
}
