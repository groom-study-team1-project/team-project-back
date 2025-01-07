package deepdivers.community.domain.hashtag.repository.jpa;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import deepdivers.community.domain.hashtag.model.PostHashtag;

@Transactional
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

	List<PostHashtag> findAllByPostId(Long postId);

	@Modifying
	@Query("DELETE FROM PostHashtag ph WHERE ph.post.id = :postId AND ph.hashtag.id IN :hashtagIds")
	void deleteByPostIdAndHashtagIds(@Param("postId") Long postId, @Param("hashtagIds") Set<Long> hashtagIds);

}


