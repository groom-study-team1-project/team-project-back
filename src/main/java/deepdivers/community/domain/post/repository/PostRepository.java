package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.vo.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

	Optional<Post> findByIdAndMemberId(Long postId, Long memberId);

	@Modifying
	@Query("""
        update Post p set p.commentCount = p.commentCount + 1
        WHERE p.id = :postId
        """)
	void incrementCommentCount(Long postId);

	Optional<Post> findByIdAndStatus(Long postId, PostStatus status);

}
