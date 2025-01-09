package deepdivers.community.domain.like.entity;

import deepdivers.community.domain.common.entity.TimeBaseEntity;
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
@Table(name = "deepdive_community_like")
public class Like extends TimeBaseEntity {

    @EmbeddedId
    private LikeId id;

    public static Like of(final Long targetId, final Long memberId, final LikeTarget likeTarget) {
        final LikeId id = new LikeId(targetId, memberId, likeTarget);
        return new Like(id);
    }

}
