package deepdivers.community.domain.hashtag.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.hashtag.exception.HashtagExceptionCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class HashtagTest {

    @Test
    void 해시태그_이름_정보로_생성할_수_있다() {
        Hashtag hashtag = Hashtag.from("해시태그");
        assertThat(hashtag.getHashtag()).isEqualTo("해시태그");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 해시태그_이름_빈_값_생성_시_예외(String name) {
        assertThatThrownBy(() -> Hashtag.from(name))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", HashtagExceptionCode.INVALID_HASHTAG_FORMAT);
    }

    @Test
    void 해시태그_틀린_정규식_생성_시_예외() {
        String name = "1".repeat(11);

        assertThatThrownBy(() -> Hashtag.from(name))
            .isInstanceOf(BadRequestException.class)
            .hasFieldOrPropertyWithValue("exceptionType", HashtagExceptionCode.INVALID_HASHTAG_FORMAT);
    }

}