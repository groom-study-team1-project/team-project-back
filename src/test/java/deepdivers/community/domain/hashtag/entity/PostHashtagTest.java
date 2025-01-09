package deepdivers.community.domain.hashtag.entity;

import static org.assertj.core.api.Assertions.assertThat;

import deepdivers.community.domain.post.entity.Post;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PostHashtagTest {

    @Test
    void post와_hashtag_정보로_생성할_수_있다() {
        // given
        Post post = Mockito.mock(Post.class);
        Hashtag hashtag = Mockito.mock(Hashtag.class);

        // when
        PostHashtag postHashtag = PostHashtag.of(post, hashtag);

        // then
        assertThat(postHashtag.getHashtag()).isEqualTo(hashtag);
        assertThat(postHashtag.getPost()).isEqualTo(post);
    }

}