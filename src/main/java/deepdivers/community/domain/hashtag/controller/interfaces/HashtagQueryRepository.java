package deepdivers.community.domain.hashtag.controller.interfaces;

import java.util.List;

public interface HashtagQueryRepository {

    List<String> findAllHashtagByPost(Long postId);


}
