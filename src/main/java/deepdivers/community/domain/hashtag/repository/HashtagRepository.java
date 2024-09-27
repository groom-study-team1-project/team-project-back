package deepdivers.community.domain.hashtag.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import deepdivers.community.domain.hashtag.model.Hashtag;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
	Optional<Hashtag> findByHashtag(String hashtag); // 해시태그 검색 메서드

	// 사용되지 않는 해시태그를 찾는 쿼리
	@Query("SELECT h FROM Hashtag h WHERE h.id NOT IN (SELECT ph.hashtag.id FROM PostHashtag ph)")
	List<Hashtag> findUnusedHashtags();
}
