package deepdivers.community.domain.hashtag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import deepdivers.community.domain.hashtag.model.PostHashtag;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
}
