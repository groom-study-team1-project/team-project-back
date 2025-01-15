package deepdivers.community.domain.post.domain;

import deepdivers.community.domain.category.entity.PostCategory;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.entity.PostContent;
import deepdivers.community.domain.post.entity.PostTitle;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class PostCreator {

    private final PostCategory category;
    private final Member member;

    public abstract PostTitle getTitle();
    public abstract PostContent getContent();
    public abstract String getThumbnailUrl();

}
