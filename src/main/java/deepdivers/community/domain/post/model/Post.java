package deepdivers.community.domain.post.model;

import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.model.vo.PostStatus;
import jakarta.persistence.*;
import java.util.stream.Collectors;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Getter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DynamicUpdate
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

    @Column(nullable = false)
    @ColumnDefault("'posts/thumbnail.png'")
    private String thumbnail;

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
    @Setter
    private PostStatus status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<PostHashtag> postHashtags = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> imageKeys = new ArrayList<>();

    @Builder
    public Post(final PostSaveRequest request, final PostCategory category, final Member member) {
        this.title = PostTitle.of(request.title());
        this.content = PostContent.of(request.content());
        this.thumbnail = request.thumbnailImageKey();
        this.category = category;
        this.member = member;
        this.commentCount = 0;
        this.likeCount = 0;
        this.viewCount = 0;
        this.status = PostStatus.ACTIVE;
    }

    public static Post of(final PostSaveRequest request, final PostCategory category, final Member member) {
        return new Post(request, category, member);
    }

    public List<String> getHashtags() {
        return postHashtags.stream()
                .map(PostHashtag::getHashtagName)
                .toList();
    }

    public Post connectHashtags(final Set<PostHashtag> postHashtags) {
        this.postHashtags = postHashtags;
        return this;
    }

    public Post connectImageKey(final List<String> postImageKeys) {
        imageKeys.removeIf(image -> true);
        postImageKeys.forEach(imageKey -> imageKeys.add(PostImage.of(this, imageKey)));
        return this;
    }

    public Post updatePost(final PostSaveRequest request, final PostCategory category) {
        this.title = PostTitle.of(request.title());
        this.content = PostContent.of(request.content());
        this.thumbnail = request.thumbnailImageKey();
        this.category = category;
        return this;
    }

    public void increaseViewCount() {
        this.viewCount += 1;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount -= 1;
        }
    }

    public List<String> getImageKeys() {
        return imageKeys.stream().map(PostImage::getImageKey).toList();
    }
}
