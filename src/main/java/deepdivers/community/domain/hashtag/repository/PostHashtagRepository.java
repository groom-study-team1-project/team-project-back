package deepdivers.community.domain.hashtag.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.post.model.Post;

@Transactional
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
	List<PostHashtag> findByPostId(Long postId);

	// 해당 Post와 연관된 모든 PostHashtag 삭제
	void deleteAllByPost(Post post);

	@Modifying
	@Query("DELETE FROM PostHashtag ph WHERE ph.post.id = :postId AND ph.hashtag.id IN :hashtagIds")
	void deleteByPostIdAndHashtagIds(@Param("postId") Long postId, @Param("hashtagIds") Set<Long> hashtagIds);

}


