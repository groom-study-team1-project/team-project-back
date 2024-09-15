package deepdivers.community.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import deepdivers.community.domain.post.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
