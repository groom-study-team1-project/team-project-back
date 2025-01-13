package deepdivers.community.domain.post.domain.adaptor;

import deepdivers.community.domain.category.entity.PostCategory;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.domain.PostCreator;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.entity.PostContent;
import deepdivers.community.domain.post.entity.PostTitle;


public class GeneralPostAdaptor extends PostCreator {

    private final PostSaveRequest request;

    public GeneralPostAdaptor(PostSaveRequest request, PostCategory category, Member member) {
        super(category, member);
        this.request = request;
    }

    public PostTitle getTitle() {
        return PostTitle.of(request.title());
    }

    public PostContent getContent() {
        return PostContent.of(request.content());
    }

    public String getThumbnailUrl() {
        return request.thumbnailImageUrl();
    }

}
