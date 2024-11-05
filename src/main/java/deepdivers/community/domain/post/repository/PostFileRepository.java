package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.post.model.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    List<PostFile> findByPostId(Long postId);

    void deleteByImageUrlAndPostId(String imageUrl, Long postId);
}