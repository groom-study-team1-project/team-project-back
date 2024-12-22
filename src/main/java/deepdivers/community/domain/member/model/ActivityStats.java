package deepdivers.community.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityStats {

    @Column(nullable = false)
    private Integer postCount;

    @Column(nullable = false)
    private Integer commentCount;

    protected static ActivityStats createDefault() {
        return new ActivityStats(0, 0);
    }

    public void incrementCommentCount() {
        commentCount++;
    }

    public void incrementPostCount() {
        postCount++;
    }
}