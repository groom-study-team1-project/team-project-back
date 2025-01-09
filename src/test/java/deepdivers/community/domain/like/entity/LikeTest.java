package deepdivers.community.domain.like.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LikeTest {

    @Test
    void memberId와_좋아요_대상_id가_같더라도_좋아요_타입이_다르다면_다른_객체이다() {
        // given
        Like commentLike = Like.of(1L, 1L, LikeTarget.COMMENT);
        Like postLike = Like.of(1L, 1L, LikeTarget.POST);

        // when
        boolean result = commentLike.equals(postLike);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void memberId와_좋아요_대상_id과_좋아요_타입이_같다면_같은_객체이다() {
        // given
        Like postLike1 = Like.of(1L, 1L, LikeTarget.POST);
        Like postLike2 = Like.of(1L, 1L, LikeTarget.POST);

        // when
        boolean result = postLike1.equals(postLike2);

        // then
        assertThat(result).isTrue();
    }

}