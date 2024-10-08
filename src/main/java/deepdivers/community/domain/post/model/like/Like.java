package deepdivers.community.domain.post.model.like;

import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.post.model.vo.LikeTarget;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "c_like")
public class Like extends BaseEntity {

    @EmbeddedId
    private LikeId id;

    public static Like of(final Long targetId, final Long memberId, final LikeTarget likeTarget) {
        final LikeId id = new LikeId(targetId, memberId, likeTarget);
        return new Like(id);
    }

}
