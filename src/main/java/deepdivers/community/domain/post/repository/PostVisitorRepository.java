package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostVisitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostVisitorRepository extends JpaRepository<PostVisitor, Long> {
	Optional<PostVisitor> findByPostAndIpAddr(Post post, String ipAddr);

	@Modifying
	@Query("""
        DELETE FROM PostVisitor pv
        WHERE pv.post.id = :postId
        """)
	void deleteAllByPostId(@Param("postId") Long postId);
}
