package deepdivers.community.domain.post.controller.dto;

import deepdivers.community.domain.post.dto.response.CountInfo;
import deepdivers.community.domain.post.dto.response.GetAllPostsResponse;
import deepdivers.community.domain.post.dto.response.MemberInfo;
import java.time.LocalDateTime;
import java.util.List;

public class GetAllPostsTestResponse {

    private Long postId;
    private String title;
    private String content;
    private Long categoryId;
    private MemberInfo memberInfo;
    private CountInfo countInfo;
    private List<String> hashtags;
    private List<String> imageUrls;
    private LocalDateTime createdAt;

    public GetAllPostsTestResponse(Long postId, String title, String content, Long categoryId, MemberInfo memberInfo,
                                   CountInfo countInfo, List<String> hashtags, List<String> imageUrls,
                                   LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.categoryId = categoryId;
        this.memberInfo = memberInfo;
        this.countInfo = countInfo;
        this.hashtags = hashtags;
        this.imageUrls = imageUrls;
        this.createdAt = createdAt;
    }

    public static GetAllPostsTestResponse from(GetAllPostsResponse response) {
        return new GetAllPostsTestResponse(
            response.getPostId(),
            response.getTitle(),
            response.getContent(),
            response.getCategoryId(),
            response.getMemberInfo(),
            response.getCountInfo(),
            response.getHashtags(),
            response.getImageUrls(),
            response.getCreatedAt()
        );
    }

}
