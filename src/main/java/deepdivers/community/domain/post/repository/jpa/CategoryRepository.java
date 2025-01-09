package deepdivers.community.domain.post.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import deepdivers.community.domain.post.entity.PostCategory;

public interface CategoryRepository extends JpaRepository<PostCategory, Long> {
}
