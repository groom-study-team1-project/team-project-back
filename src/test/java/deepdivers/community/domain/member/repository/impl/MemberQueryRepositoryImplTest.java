package deepdivers.community.domain.member.repository.impl;

import static org.junit.jupiter.api.Assertions.*;

import deepdivers.community.global.config.JpaConfig;
import deepdivers.community.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@Import({JpaConfig.class, QueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext
class MemberQueryRepositoryImplTest {

    @Autowired
    private EntityManager em;

}