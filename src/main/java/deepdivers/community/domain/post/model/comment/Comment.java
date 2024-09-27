package deepdivers.community.domain.post.model.comment;

import deepdivers.community.domain.post.dto.request.WriteReplyRequest;
import deepdivers.community.domain.post.model.vo.CommentStatus;
import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.model.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
                @Index(name = "idx_comment_member_id_id_status", columnList = "memberId, id, status")
        }
)
@DynamicInsert
@DynamicUpdate
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

    @Column(updatable = false)
    private Long parentCommentId;

    @Embedded
    private CommentContent content;

    @ColumnDefault("0")
    private Integer replyCount;

    @ColumnDefault("0")
    private Integer likeCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private CommentStatus status;

    protected Comment(final Post post, final Member member, final String content) {
        this.post = post;
        this.member = member;
        this.content = new CommentContent(content);
        this.status = CommentStatus.REGISTERED;
    }

    protected Comment(final Post post, final Member member, final WriteReplyRequest request) {
        this.post = post;
        this.member = member;
        this.content = new CommentContent(request.content());
        this.parentCommentId = request.commentId();
        this.status = CommentStatus.REGISTERED;
    }

    public static Comment of(final Post post, final Member member, final String content) {
        return new Comment(post, member, content);
    }

    public static Comment of(final Post post, final Member member, final WriteReplyRequest request) {
        return new Comment(post, member, request);
    }

    public void updateComment(final String content) {
        this.content.updateContent(content);
    }

    public void deleteComment() {
        this.status = CommentStatus.UNREGISTERED;
    }

}
