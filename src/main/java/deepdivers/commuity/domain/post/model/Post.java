package deepdivers.commuity.domain.post.model;

import deepdivers.commuity.domain.hashtag.model.PostHashtag;
import deepdivers.commuity.domain.post.model.vo.PostStatus;
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
                @Index(name = "idx_post_view_count_id", columnList = "view_count, id"),
                @Index(name = "idx_post_recommend_count_id", columnList = "recommend_count, id"),
                @Index(name = "idx_post_category_id_id", columnList = "category_id, id"),
                @Index(name = "idx_post_created_at", columnList = "created_at"),
                @Index(name = "idx_post_member_id_id", columnList = "member_id, id"),
                @Index(name = "idx_post_category_id_member_id_id", columnList = "category_id, member_id, id")
        }
)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 10000)
    private String content;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer commentCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer recommendCount;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer viewCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostHashtag> postHashtags = new HashSet<>();

}
