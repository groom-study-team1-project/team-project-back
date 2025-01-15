package deepdivers.community.domain.post.repository.utils;

import static deepdivers.community.domain.like.entity.QLike.like;
import static deepdivers.community.domain.member.entity.QMember.member;
import static deepdivers.community.domain.post.entity.QPost.post;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import deepdivers.community.domain.like.entity.QLike;
import deepdivers.community.domain.member.entity.QMember;
import deepdivers.community.domain.post.dto.response.AuthorInformationResponse;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.entity.QPost;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostQBeanGenerator {

    public static <T extends PostPreviewResponse> QBean<T> createPreview(final Class<T> type) {
        return Projections.fields(
            type,
            post.id.as("postId"),
            post.category.id.as("categoryId"),
            post.title.title.as("title"),
            post.thumbnail.as("thumbnail"),
            post.content.content.as("content"),
            post.createdAt.as("createdAt"),
            post.viewCount.as("viewCount"),
            post.likeCount.as("likeCount"),
            post.commentCount.as("commentCount"),
            Projections.fields(AuthorInformationResponse.class,
                member.id.as("memberId"),
                member.nickname.value.as("nickname"),
                member.image.imageUrl.as("imageUrl"),
                member.job.as("memberJob")
            ).as("authorInformation")
        );
    }
    
    public static <T extends PostPreviewResponse> QBean<T> createPostDetail(final Class<T> type, final Long viewerId) {
        return Projections.fields(
            type,
            post.id.as("postId"),
            post.category.id.as("categoryId"),
            post.title.title.as("title"),
            post.thumbnail.as("thumbnail"),
            post.content.content.as("content"),
            post.createdAt.as("createdAt"),
            post.viewCount.as("viewCount"),
            post.likeCount.as("likeCount"),
            post.commentCount.as("commentCount"),
            like.isNotNull().as("isLikedMe"),
            post.member.id.eq(viewerId).as("isWroteMe"),
            Projections.fields(AuthorInformationResponse.class,
                member.id.as("memberId"),
                member.nickname.value.as("nickname"),
                member.image.imageUrl.as("imageUrl"),
                member.job.as("memberJob")
            ).as("authorInformation")
        );
    }

}
