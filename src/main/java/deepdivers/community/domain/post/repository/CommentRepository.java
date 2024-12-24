package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.post.model.comment.Comment;
import deepdivers.community.domain.post.model.vo.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Modifying
    @Query("""
        update Comment c set c.replyCount = c.replyCount + 1
        WHERE c.id = :commentId
        """)
    void incrementReplyCount(Long commentId);

    @Modifying
    @Query("""
        update Comment c set c.likeCount = c.likeCount + 1
        WHERE c.id = :commentId
        """)
    void incrementLikeCount(Long commentId);

    @Modifying
    @Query("""
        update Comment c set c.likeCount = c.likeCount - 1
        WHERE c.id = :commentId
        """)
    void decrementLikeCount(Long commentId);

    @Modifying
    @Query("""
        update Comment c
        set c.status = :status
        where c.id = :commentId
        """)
    void deleteComment(Long commentId, CommentStatus status);

    @Modifying
    @Query("""
        update Comment c
        set c.replyCount = c.replyCount - 1
        where c.id = :parentId and c.replyCount > 0
    """)
    void decrementReplyCount(Long parentId);

}
