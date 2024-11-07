package deepdivers.community.domain.post.model;

import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.hashtag.model.Hashtag;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.model.vo.PostStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private PostCategory category;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer commentCount = 0;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer likeCount = 0;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostHashtag> postHashtags = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostFile> postFiles = new ArrayList<>();

    public List<String> getHashtags() {
        return postHashtags.stream()
            .map(postHashtag -> postHashtag.getHashtag().getName())
            .collect(Collectors.toList());
    }

    public List<String> getImageUrl() {
        return postFiles.stream()
                .map(PostFile::getImageUrl)
                .toList();
    }

    @Builder
    public Post(PostTitle title, PostContent content, PostCategory category, Member member, PostStatus status) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.member = member;
        this.commentCount = 0;
        this.likeCount = 0;
        this.viewCount = 0;
        this.status = status != null ? status : PostStatus.ACTIVE;
    }

    public static Post of(final PostCreateRequest request, final PostCategory category, final Member member) {
        return new Post(
            PostTitle.of(request.title()),
            PostContent.of(request.content()),
            category,
            member,
            PostStatus.ACTIVE
        );
    }

    public void updatePost(PostTitle title, PostContent content, PostCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void increaseViewCount() {
        this.viewCount += 1;
    }

    public boolean isActive() {
        return this.status == PostStatus.ACTIVE;
    }

    public boolean isDeleted() {
        return this.status == PostStatus.DELETED;
    }

    public void delete() {
        this.status = PostStatus.DELETED;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount -= 1;
        }
    }
}
