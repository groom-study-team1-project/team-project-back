package deepdivers.community.domain.hashtag.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import deepdivers.community.domain.hashtag.entity.PostHashtag;

@Transactional
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

	List<PostHashtag> findAllByPostId(Long postId);

}


