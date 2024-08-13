package deepdivers.commuity.domain.post.model;

import deepdivers.commuity.domain.post.model.vo.PostStatus;

public class Post {

    private Long id;
    private String title;
    private String content;
    private Integer commentCount;
    private Integer recommendCount;
    private Integer viewCount;
    private PostStatus status;

}
