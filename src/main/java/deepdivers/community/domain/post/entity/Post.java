package deepdivers.community.domain.post.entity;

import deepdivers.community.domain.category.entity.PostCategory;
import deepdivers.community.domain.common.entity.TimeBaseEntity;
import deepdivers.community.domain.common.exception.NotFoundException;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import deepdivers.community.domain.post.domain.PostCreator;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

@Slf4j
@Getter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DynamicUpdate
public class Post extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Embedded
    private PostTitle title;

    @Embedded
    private PostContent content;

    @Column(nullable = false)
    @ColumnDefault("'posts/thumbnail.png'")
    private String thumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private PostCategory category;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer commentCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer likeCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer viewCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    protected Post(final PostCreator creator) {
        this.title = creator.getTitle();
        this.content = creator.getContent();
        this.thumbnail = creator.getThumbnailUrl();
        this.category = creator.getCategory();
        this.member = creator.getMember();
        this.commentCount = 0;
        this.likeCount = 0;
        this.viewCount = 0;
        this.status = PostStatus.ACTIVE;
    }

    public static Post of(final PostCreator creator) {
        creator.getMember().incrementPostCount();
        return new Post(creator);
    }

    public Post update(final PostCreator creator) {
        this.title = creator.getTitle();
        this.content = creator.getContent();
        this.thumbnail = creator.getThumbnailUrl();
        this.category = creator.getCategory();
        return this;
    }

    public void deletePost() {
        if (this.status == PostStatus.DELETED) {
            throw new NotFoundException(PostExceptionCode.POST_NOT_FOUND);
        }
        this.status = PostStatus.DELETED;
    }

}
