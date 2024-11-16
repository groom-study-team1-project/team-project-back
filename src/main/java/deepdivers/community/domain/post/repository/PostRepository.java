package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.model.Post;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByMember(Member member);
	List<Post> findByCategoryId(Long categoryId);
	Optional<Post> findByIdAndMemberId(Long postId, Long memberId);

	@Modifying
	@Query("""
        update Post p set p.commentCount = p.commentCount + 1
        WHERE p.id = :postId
        """)
	void incrementCommentCount(Long postId);

	@Modifying
	@Query("DELETE FROM Comment c WHERE c.post.id = :postId")
	void deleteAllByPost(Post post);

}
