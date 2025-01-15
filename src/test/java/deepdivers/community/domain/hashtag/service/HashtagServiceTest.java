package deepdivers.community.domain.hashtag.service;

import static org.assertj.core.api.Assertions.assertThat;

import deepdivers.community.domain.IntegrationTest;
import deepdivers.community.domain.hashtag.entity.Hashtag;
import deepdivers.community.domain.post.entity.Post;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HashtagServiceTest extends IntegrationTest {

    @Autowired HashtagService hashtagService;

    @Test
    void 게시글에_해시태그가_추가된다() {
        // given
        Post post = getPost(1L);
        List<String> hashTags = List.of("해시태그1", "해시태그2", "해시태그3");

        // when
        hashtagService.createPostHashtags(post, hashTags);

        // then
        List<String> savedHashtags = getHashtagsByPostId(post.getId()).stream()
            .map(postHashtag -> postHashtag.getHashtag().getHashtag()).toList();

        assertThat(savedHashtags).containsAll(hashTags);
    }

    @Test
    void 게시글에_연결된_기존_해시태그_정보는_끊어지고_새로운_해시태그로_등록이_된다() {
        // given, test.sql
        Post post = getPost(10L);
        List<String> hashTags = List.of("해시태그1", "해시태그2", "해시태그3");

        // when
        hashtagService.updatePostHashtags(post, hashTags);

        // then
        List<String> savedHashtags = getHashtagsByPostId(post.getId()).stream()
            .map(postHashtag -> postHashtag.getHashtag().getHashtag()).toList();

        assertThat(hashTags).isEqualTo(savedHashtags);
    }

    @Test
    void 게시글에_연결이_끊어진_해시태그는_제거되지_않는다() {
        // given, test.sql
        Post post = getPost(1L);
        List<String> hashTags = List.of("해시태그1", "해시태그2", "해시태그3");

        // when
        hashtagService.updatePostHashtags(post, hashTags);

        // then
        Hashtag hashtag = getHashtag(1L);
        assertThat(hashtag.getHashtag()).isEqualTo("Spring");
    }

}