package deepdivers.community.domain.hashtag.repository;

import static org.assertj.core.api.Assertions.assertThat;

import deepdivers.community.domain.RepositoryTest;
import deepdivers.community.domain.hashtag.dto.PopularHashtagResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

class HashtagQueryRepositoryImplTest extends RepositoryTest {

    @Test
    void 일반_게시판_인기_해시태그_조회() {
        // given, test.sql 자유게시판에는 Spring, JPA, React가 있음
        // when
        List<PopularHashtagResponse> hashtags = hashtagQueryRepository.findWeeklyPopularHashtagByCategory(1L);

        // then
        assertThat(hashtags).hasSize(3);
    }

    @Test
    void 프로젝트_게시판_인기_해시태그_조회() {
        // 프로젝트 게시판에는 Spring, QueryDSL, AWS가 있음
        // given, test.sql
        // when
        List<PopularHashtagResponse> hashtags = hashtagQueryRepository.findWeeklyPopularHashtagByCategory(2L);

        // then
        assertThat(hashtags).hasSize(3);
    }

    @Test
    void 삭제된_게시글의_해시태그는_제외된다() {
        // post_id=3은 DELETED 상태이고 Spring, Java 태그를 가짐
        // given
        // when
        List<PopularHashtagResponse> hashtags = hashtagQueryRepository.findWeeklyPopularHashtagByCategory(3L);

        // then
        assertThat(hashtags).hasSize(1);
    }

    @Test
    void 존재하지_않는_카테고리는_빈_리스트_반환() {
        // given
        // when
        List<PopularHashtagResponse> hashtags = hashtagQueryRepository.findWeeklyPopularHashtagByCategory(999L);

        // then
        assertThat(hashtags).isEmpty();
    }

    @Test
    void 일주일이_지난_해시태그는_조회되지_않음() {
        // given
        // when
        List<PopularHashtagResponse> hashtags = hashtagQueryRepository.findWeeklyPopularHashtagByCategory(4L);

        // then
        assertThat(hashtags).hasSize(1);
    }

}