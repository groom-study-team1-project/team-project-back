package deepdivers.community.domain.member.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ActivityStatsTest {

    @Test
    @DisplayName("올바른 전화번호 입력 시 Contact 객체를 성공적으로 생성하는 것을 확인한다.")
    void createDefaultShouldReturnActivityStatsWithZero() {
        // when
        ActivityStats stats = ActivityStats.createDefault();
        // then
        assertThat(stats).isNotNull();
        assertThat(stats.getCommentCount()).isZero();
        assertThat(stats.getPostCount()).isZero();
    }

}