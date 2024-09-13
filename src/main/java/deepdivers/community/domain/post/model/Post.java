package deepdivers.community.domain.post.model;

import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.post.model.hashtag.HashtagRelation;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.model.vo.PostStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        indexes = {
                @Index(name = "idx_post_title", columnList = "title"),
                @Index(name = "idx_post_view_count_id", columnList = "viewCount, id"),
                @Index(name = "idx_post_recommend_count_id", columnList = "recommendCount, id"),
                @Index(name = "idx_post_category_id_id", columnList = "categoryId, id"),
                @Index(name = "idx_post_created_at", columnList = "createdAt"),
                @Index(name = "idx_post_member_id_id", columnList = "memberId, id"),
                @Index(name = "idx_post_category_id_member_id_id", columnList = "categoryId, memberId, id")
        }
)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 10000)
    private String content;

    @ColumnDefault("0")
    @Column(nullable = false)
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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HashtagRelation> hashtagRelations = new HashSet<>();

}
