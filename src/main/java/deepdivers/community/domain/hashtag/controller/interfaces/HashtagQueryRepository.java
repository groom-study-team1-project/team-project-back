package deepdivers.community.domain.hashtag.controller.interfaces;

import java.util.List;
import java.util.Map;

public interface HashtagQueryRepository {

    List<String> findAllHashtagByPost(Long postId);

    Map<Long, List<String>> findAllHashtagByPosts(List<Long> postIds);


}
