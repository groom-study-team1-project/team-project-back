package deepdivers.community.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.model.Post;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByMember(Member member);
	List<Post> findByCategoryId(Long categoryId);

	@Modifying
	@Query("""
        update Post p set p.commentCount = p.commentCount + 1
        WHERE p.id = :postId
        """)
	void incrementCommentCount(Long postId);

}
