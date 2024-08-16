package deepdivers.community.global.config;

import deepdivers.community.domain.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
class JpaConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private EntityManager entityManager;

    @Entity
    @Table(name = "test_entity")
    public static class TestEntity extends BaseEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    }

    @Test
    @Transactional
    public void testJpaAuditingEnabled() {
        TestEntity entity = new TestEntity();

        entityManager.persist(entity);
        entityManager.flush();

        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    public void contextLoads() {
        assertNotNull(context);
        assertTrue(context.containsBean("jpaConfig"));
    }

}