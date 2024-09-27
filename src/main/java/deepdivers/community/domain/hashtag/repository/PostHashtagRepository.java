package deepdivers.community.domain.hashtag.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.post.model.Post;

@Transactional
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
	// 해당 Post와 연관된 모든 PostHashtag 삭제
	void deleteAllByPost(Post post);
}


