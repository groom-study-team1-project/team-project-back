package deepdivers.community.domain.hashtag.controller.interfaces;

import deepdivers.community.domain.category.entity.CategoryType;
import deepdivers.community.domain.hashtag.dto.PopularHashtagResponse;
import java.util.List;
import java.util.Map;

public interface HashtagQueryRepository {

    List<String> findAllHashtagByPost(Long postId);

    Map<Long, List<String>> findAllHashtagByPosts(List<Long> postIds);

    List<PopularHashtagResponse> findWeeklyPopularHashtagByCategory(Long categoryId, CategoryType categoryType);

}
