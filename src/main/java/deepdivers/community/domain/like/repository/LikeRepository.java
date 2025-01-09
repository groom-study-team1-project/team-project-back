package deepdivers.community.domain.like.repository;

import deepdivers.community.domain.like.entity.Like;
import deepdivers.community.domain.like.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, LikeId> {
}
