package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostVisitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostVisitorRepository extends JpaRepository<PostVisitor, Long> {
	Optional<PostVisitor> findByPostAndIpAddr(Post post, String ipAddr);
}
