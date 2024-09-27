package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.post.model.like.Like;
import deepdivers.community.domain.post.model.like.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, LikeId> {
}
