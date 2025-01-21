package deepdivers.community.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.category.controller.interfaces.CategoryQueryRepository;
import deepdivers.community.domain.category.repository.CategoryQueryRepositoryImpl;
import deepdivers.community.domain.hashtag.controller.interfaces.HashtagQueryRepository;
import deepdivers.community.domain.hashtag.repository.HashtagQueryRepositoryImpl;
import deepdivers.community.domain.file.application.interfaces.FileQueryRepository;
import deepdivers.community.domain.file.repository.FileQueryRepositoryImpl;
import deepdivers.community.domain.member.controller.interfaces.MemberQueryRepository;
import deepdivers.community.domain.member.repository.MemberQueryRepositoryImpl;
import deepdivers.community.domain.post.controller.interfaces.PostQueryRepository;
import deepdivers.community.domain.post.controller.interfaces.ProjectPostQueryRepository;
import deepdivers.community.domain.post.repository.PostQueryRepositoryImpl;
import deepdivers.community.domain.post.repository.ProjectPostQueryRepositoryImpl;
import deepdivers.community.global.config.JpaConfig;
import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.global.config.QueryDslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@Import({
    JpaConfig.class,
    QueryDslConfig.class,
    HashtagQueryRepositoryImpl.class,
    FileQueryRepositoryImpl.class,
    PostQueryRepositoryImpl.class,
    MemberQueryRepositoryImpl.class,
    FileQueryRepositoryImpl.class,
    CategoryQueryRepositoryImpl.class,
    ProjectPostQueryRepositoryImpl.class,
    HashtagQueryRepositoryImpl.class,
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext
public class RepositoryTest {

    @Autowired protected JPAQueryFactory jpaQueryFactory;
    @Autowired protected HashtagQueryRepository hashtagQueryRepository;
    @Autowired protected FileQueryRepository fileQueryRepository;
    @Autowired protected PostQueryRepository postQueryRepository;
    @Autowired protected MemberQueryRepository memberQueryRepository;
    @Autowired protected CategoryQueryRepository categoryQueryRepository;
    @Autowired protected ProjectPostQueryRepository projectPostQueryRepository;
    @Autowired protected HashtagQueryRepository hashTagQueryRepository;

}
