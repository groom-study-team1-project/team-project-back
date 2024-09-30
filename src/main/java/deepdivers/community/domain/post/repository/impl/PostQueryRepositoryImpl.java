package deepdivers.community.domain.post.repository.impl;

import static deepdivers.community.domain.member.model.QMember.*;
import static deepdivers.community.domain.post.model.QPost.*;
import static deepdivers.community.domain.post.model.like.QLike.*;
import static deepdivers.community.domain.hashtag.model.QHashtag.hashtag1; // QHashtag import 수정
import static deepdivers.community.domain.hashtag.model.QPostHashtag.postHashtag; // QPostHashtag import 추가

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import deepdivers.community.domain.member.dto.response.AllMyPostsResponse;
import deepdivers.community.domain.post.dto.response.CountInfo;
import deepdivers.community.domain.post.dto.response.MemberInfo;
import deepdivers.community.domain.post.dto.response.PostAllReadResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.model.vo.LikeTarget;
import deepdivers.community.domain.post.repository.PostQueryRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AllMyPostsResponse> findAllMyPosts(
        final Long memberId,
        final Long lastContentId,
        final Long categoryId
    ) {
        return queryFactory.select(
                Projections.fields(
                    AllMyPostsResponse.class,
                    post.id.as("id"),
                    post.title.title.as("title"),
                    post.viewCount.as("viewCount"),
                    post.likeCount.as("likeCount"),
                    post.commentCount.as("commentCount"),
                    post.createdAt.as("createdAt"),
                    member.id.as("memberId"),
                    member.nickname.value.as("memberNickname"),
                    member.job.as("memberJob"),
                    like.isNotNull().as("isLikedMe")
                ))
            .from(post)
            .join(member).on(post.member.id.eq(member.id))
            .leftJoin(like).on(hasLike(memberId))
            .where(
                post.category.id.eq(categoryId),
                post.member.id.eq(memberId),
                post.id.lt(lastContentId)
            )
            .orderBy(post.id.desc())
            .limit(5)
            .fetch();
    }

    private BooleanExpression hasLike(final Long memberId) {
        return post.id
            .eq(like.id.targetId)
            .and(like.id.targetType.eq(LikeTarget.POST))
            .and(like.id.memberId.eq(memberId));
    }

    @Override
    public List<PostAllReadResponse> findAllPosts(Long lastContentId, Long categoryId) {

        List<PostAllReadResponse> posts = queryFactory.select(
                Projections.fields(
                    PostAllReadResponse.class,
                    post.id.as("postId"),
                    post.title.title.as("title"),
                    post.content.content.as("content"),
                    post.category.id.as("categoryId"),
                    Projections.fields(MemberInfo.class,
                        member.id.as("memberId"),
                        member.nickname.value.as("nickname"),
                        member.imageUrl.as("imageUrl"),
                        member.job.as("memberJob")
                    ).as("memberInfo"),
                    Projections.fields(CountInfo.class,
                        post.viewCount.as("viewCount"),
                        post.likeCount.as("likeCount"),
                        post.commentCount.as("commentCount")
                    ).as("countInfo"),
                    Expressions.stringTemplate("FORMATDATETIME({0}, 'yyyy-MM-dd HH:mm:ss')", post.createdAt).as("createdAt")
                ))
            .from(post)
            .join(member).on(post.member.id.eq(member.id))
            .where(
                post.id.lt(lastContentId),
                categoryId != null ? post.category.id.eq(categoryId) : null
            )
            .orderBy(post.id.desc())
            .limit(10)
            .fetch();

        for (PostAllReadResponse postResponse : posts) {
            List<String> hashtags = queryFactory
                .select(postHashtag.hashtag.hashtag)
                .from(postHashtag)
                .join(hashtag1).on(postHashtag.hashtag.id.eq(hashtag1.id))
                .where(postHashtag.post.id.eq(postResponse.getPostId()))
                .fetch();

            postResponse.setHashtags(hashtags);
        }
        return posts;
    }
}
