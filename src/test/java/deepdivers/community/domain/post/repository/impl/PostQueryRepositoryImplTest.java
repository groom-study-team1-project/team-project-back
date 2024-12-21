package deepdivers.community.domain.post.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.post.dto.response.GetAllPostsResponse;
import deepdivers.community.domain.post.repository.PostQueryRepository;
import deepdivers.community.global.config.JpaConfig;
import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.global.config.QueryDslConfig;
import deepdivers.community.infra.aws.s3.S3PresignManager;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@Import({JpaConfig.class, QueryDslConfig.class, LocalStackTestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext
class PostQueryRepositoryImplTest {

    @Autowired
    private EntityManager em;
    @MockBean
    private S3PresignManager s3PresignManager;
    private PostQueryRepository postQueryRepository;

    @BeforeEach
    void setUp() {
        postQueryRepository = new PostQueryRepositoryImpl(new JPAQueryFactory(em), s3PresignManager);
        when(s3PresignManager.generateAccessUrl(anyString())).thenAnswer(image -> "image");
    }

    @Test
    @DisplayName("카테고리 정보와 마지막 포스트 정보가 없을 경우 전체 게시글 중 최신 10개가 조회된다.")
    void givenNullLastPostIdAndNullCategoryIdWhenFindAllPostsThenReturnTenPosts() {
        // given, test.sql
        // when
        List<GetAllPostsResponse> result = postQueryRepository.findAllPosts(null, null);

        // then
        assertThat(result).hasSize(10);
    }

    @Test
    @DisplayName("카테고리 정보만 있을 경우 카테고리 별 삭제되지 않은 전체 게시글 목록이 조회가 된다.")
    void givenNullLastPostIdAndCategoryIdWhenFindAllPostsThenReturnPostsByCategory() {
        // given, test.sql
        // when
        List<GetAllPostsResponse> result = postQueryRepository.findAllPosts(null, 1L);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("마지막 포스트 정보만 있을 경우 모든 카테고리의 삭제되지 않은 게시글 목록이 조회가 된다.")
    void givenLastPostIdAndNullCategoryIdWhenFindAllPostsThenReturnPostsAmongPostIdSmallerThanLastPostId() {
        // given, test.sql

        // when
        List<GetAllPostsResponse> result = postQueryRepository.findAllPosts(5L, null);

        // then
        assertThat(result).hasSize(4);
    }

    @Test
    @DisplayName("마지막 포스트 정보와 카테 고리 정보가 있을 경우 카테고리 별 삭제되지 않은 게시글 목록이 조회가 된다.")
    void givenLastPostIdAndCategoryIdWhenFindAllPostsThenReturnNoDeletePosts() {
        // given, test.sql
        // when
        List<GetAllPostsResponse> result = postQueryRepository.findAllPosts(5L, 1L);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("마지막 게시글 id가 1번일 경우, 조회 시 0개가 조회된다.")
    void givenLastPostIdWhenFindAllPostsThenReturnEmptyPosts() {
        // given
        Long lastPostId = 1L;

        // when
        List<GetAllPostsResponse> result = postQueryRepository.findAllPosts(lastPostId, null);

        // then
        assertThat(result).hasSize(0);
    }

}