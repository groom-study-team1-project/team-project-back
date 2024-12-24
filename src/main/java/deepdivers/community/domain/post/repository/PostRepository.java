package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.vo.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Modifying
	@Query("""
        update Post p set p.commentCount = p.commentCount + 1
        WHERE p.id = :postId
        """)
	void incrementCommentCount(Long postId);

	Optional<Post> findByIdAndStatus(Long postId, PostStatus status);

	@Modifying
	@Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
	void incrementLikeCount(Long postId);

	@Modifying
	@Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId")
	void decrementLikeCount(Long postId);

	@Modifying
	@Query("""
		UPDATE Post p
		SET p.commentCount = p.commentCount - 1
		WHERE p.id = :postId and p.commentCount > 0
		""")
	void decrementCommentCount(Long postId);

}
