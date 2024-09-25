package deepdivers.community.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import deepdivers.community.domain.post.model.PostCategory;

public interface CategoryRepository extends JpaRepository<PostCategory, Long> {
}
