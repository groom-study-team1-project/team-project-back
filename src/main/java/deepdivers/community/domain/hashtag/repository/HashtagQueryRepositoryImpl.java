package deepdivers.community.domain.hashtag.repository;

import static deepdivers.community.domain.hashtag.entity.QHashtag.hashtag1;
import static deepdivers.community.domain.hashtag.entity.QPostHashtag.postHashtag;

import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.hashtag.controller.interfaces.HashtagQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HashtagQueryRepositoryImpl implements HashtagQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<String> findAllHashtagByPost(final Long postId) {
        return queryFactory
            .select(hashtag1.hashtag)
            .from(postHashtag)
            .leftJoin(hashtag1).on(postHashtag.hashtag.id.eq(hashtag1.id))
            .where(postHashtag.post.id.eq(postId))
            .fetch();
    }

}
