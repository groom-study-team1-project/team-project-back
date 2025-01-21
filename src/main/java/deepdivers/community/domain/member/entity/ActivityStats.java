package deepdivers.community.domain.member.entity;

import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.member.exception.MemberExceptionCode;
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

    public void decrementPostCount() {
        if (postCount <= 0) {
            throw new BadRequestException(MemberExceptionCode.INVALID_ACCESS);
        }
        postCount--;
    }

}