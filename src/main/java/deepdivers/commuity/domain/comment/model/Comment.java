package deepdivers.commuity.domain.comment.model;

import deepdivers.commuity.domain.comment.model.vo.CommentStatus;
import deepdivers.commuity.domain.common.BaseEntity;
import deepdivers.commuity.domain.member.model.Member;
import deepdivers.commuity.domain.post.model.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
                @Index(name = "idx_comment_member_id_id_status", columnList = "member_id, id, status")
        }
)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String content;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer recommendCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status;

}
