package deepdivers.community.domain.hashtag.application.interfaces;

import java.util.List;
import java.util.Map;

public interface HashtagQueryRepository {

    Map<Long, List<String>> findAllHashtagByPosts(List<Long> postIds);
    List<String> findAllHashtagByPost(Long postId);

}
