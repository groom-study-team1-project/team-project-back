package deepdivers.community.domain.hashtag.repository.jpa;

import deepdivers.community.domain.hashtag.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
	Optional<Hashtag> findByHashtag(String hashtag);

	@Query("SELECT h.id FROM Hashtag h WHERE h.hashtag IN :hashtagNames")
	Set<Long> findHashtagIdsByNames(@Param("hashtagNames") Set<String> hashtagNames);

}
