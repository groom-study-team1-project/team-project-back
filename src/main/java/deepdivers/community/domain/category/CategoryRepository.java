package deepdivers.community.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;

import deepdivers.community.domain.category.entity.PostCategory;

public interface CategoryRepository extends JpaRepository<PostCategory, Long> {
}
