package deepdivers.community.domain.hashtag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import deepdivers.community.domain.hashtag.model.Hashtag;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
	Optional<Hashtag> findByHashtag(String hashtag); // 해시태그 검색 메서드
}
