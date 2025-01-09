package deepdivers.community.domain.post.repository.jpa;

import deepdivers.community.domain.post.entity.like.Like;
import deepdivers.community.domain.post.entity.like.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, LikeId> {
}
