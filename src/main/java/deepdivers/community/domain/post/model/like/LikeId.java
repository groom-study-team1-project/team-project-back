package deepdivers.community.domain.post.model.like;

import deepdivers.community.domain.post.model.vo.LikeTarget;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeId {

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private LikeTarget targetType;

    public static LikeId of(Long targetId, Long memberId, LikeTarget targetType) {
        return new LikeId(targetId, memberId, targetType);
    }
}
