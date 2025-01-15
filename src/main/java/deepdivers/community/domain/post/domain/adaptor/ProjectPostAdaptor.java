package deepdivers.community.domain.post.domain.adaptor;

import deepdivers.community.domain.category.entity.PostCategory;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.domain.PostCreator;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import deepdivers.community.domain.post.entity.PostContent;
import deepdivers.community.domain.post.entity.PostTitle;

public class ProjectPostAdaptor extends PostCreator {

    private final ProjectPostRequest dto;

    public ProjectPostAdaptor(ProjectPostRequest dto, PostCategory category, Member member) {
        super(category, member);
        this.dto = dto;
    }

    public PostTitle getTitle() {
        return PostTitle.of(dto.title());
    }

    public PostContent getContent() {
        return PostContent.of(dto.content());
    }

    public String getThumbnailUrl() {
        return dto.thumbnailImageUrl();
    }

}
