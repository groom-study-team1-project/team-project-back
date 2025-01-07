package deepdivers.community.domain.post.repository.generator;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import deepdivers.community.domain.member.model.QMember;
import deepdivers.community.domain.post.dto.response.AuthorInformationResponse;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.model.QPost;
import deepdivers.community.domain.post.model.like.QLike;

public class PostQBeanGenerator {

    public static QBean<PostPreviewResponse> createPostPreview(final QPost post, final QMember member) {
        return Projections.fields(
            PostPreviewResponse.class,
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
    
    public static QBean<PostDetailResponse> createPostDetail(
        final QPost post, 
        final QMember member, 
        final QLike like, 
        final Long viewerId
    ) {
        return Projections.fields(
            PostDetailResponse.class,
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
