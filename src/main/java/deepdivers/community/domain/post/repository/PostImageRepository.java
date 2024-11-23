package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.post.model.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostId(Long postId);

    void deleteByPostIdAndImageUrl(Long postId, String imageUrl);
}