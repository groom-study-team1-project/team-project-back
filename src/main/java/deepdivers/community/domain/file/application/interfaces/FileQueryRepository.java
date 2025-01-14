package deepdivers.community.domain.file.application.interfaces;

import static deepdivers.community.domain.file.repository.entity.QFile.file;
import static deepdivers.community.domain.post.entity.QPost.post;

import com.querydsl.core.group.GroupBy;
import deepdivers.community.domain.file.repository.entity.FileType;
import java.util.List;
import java.util.Map;

public interface FileQueryRepository {

    List<String> findAllImageUrlsByPost(Long postId);
    Map<Long, List<String>> findAllSlideImageUrlByPosts(List<Long> postIds);

}
