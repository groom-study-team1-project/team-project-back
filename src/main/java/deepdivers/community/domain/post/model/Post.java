package deepdivers.community.domain.post.model;

import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.model.vo.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;

@Getter
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer commentCount = 0;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer recommendCount = 0;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostHashtag> postHashtags = new HashSet<>();

    @Builder
    public Post(String title, String content, Category category, Member member, PostStatus status) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.member = member;
        this.commentCount = 0;
        this.recommendCount = 0;
        this.viewCount = 0;
        this.status = status != null ? status : PostStatus.ACTIVE;
    }

    // 게시글이 활성 상태인지 확인하는 메서드
    public boolean isActive() {
        return this.status == PostStatus.ACTIVE;
    }

    // 게시글이 삭제 상태인지 확인하는 메서드
    public boolean isDeleted() {
        return this.status == PostStatus.DELETED;
    }

    // 게시글 삭제 처리
    public void delete() {
        this.status = PostStatus.DELETED;
    }
}
