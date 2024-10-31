package deepdivers.community.domain.hashtag.repository;

import deepdivers.community.domain.hashtag.model.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

	List<PostHashtag> findAllByPostId(Long postId);

}


