package deepdivers.community.domain.hashtag.application.interfaces;

import java.util.List;

public interface HashtagQueryRepository {

    List<String> findAllHashtagByPost(Long postId);

}
