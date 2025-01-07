package deepdivers.community.domain.hashtag.repository;

import static deepdivers.community.domain.hashtag.model.QHashtag.hashtag1;
import static deepdivers.community.domain.hashtag.model.QPostHashtag.postHashtag;
import static deepdivers.community.domain.post.model.QPost.post;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.hashtag.application.interfaces.HashtagQueryRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HashtagQueryRepositoryImpl implements HashtagQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Map<Long, List<String>> findAllHashtagByPosts(final List<Long> postIds) {
        return queryFactory.select(post.id, hashtag1.hashtag)
            .from(postHashtag)
            .leftJoin(post).on(postHashtag.post.id.eq(post.id))
            .leftJoin(hashtag1).on(postHashtag.hashtag.id.eq(hashtag1.id))
            .where(post.id.in(postIds))
            .transform(GroupBy.groupBy(post.id)
                .as(GroupBy.list(hashtag1.hashtag)));
    }

    public List<String> findAllHashtagByPost(final Long postId) {
        return queryFactory
            .select(hashtag1.hashtag)
            .from(postHashtag)
            .leftJoin(hashtag1).on(postHashtag.hashtag.id.eq(hashtag1.id))
            .where(postHashtag.post.id.eq(postId))
            .fetch();
    }

}
