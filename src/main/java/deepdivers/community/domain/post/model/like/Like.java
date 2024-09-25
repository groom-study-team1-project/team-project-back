package deepdivers.community.domain.post.model.like;

import deepdivers.community.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "c_like")
public class Like extends BaseEntity {

    @EmbeddedId
    private LikeId id;

}
