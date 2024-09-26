package deepdivers.community.domain.post.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "ipAddr"}))
public class PostVisitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String ipAddr;

    @Column(nullable = false)
    private LocalDateTime visitedAt;

    public PostVisitor(Post post, String ipAddr) {
        this.post = post;
        this.ipAddr = ipAddr;
        this.visitedAt = LocalDateTime.now();  // 처음 방문 시간 설정
    }

    public boolean canIncreaseViewCount() {
        boolean canIncrease = visitedAt == null || visitedAt.isBefore(LocalDateTime.now().minusMinutes(30));
        System.out.println("canIncreaseViewCount 호출: visitedAt = " + visitedAt + ", canIncrease = " + canIncrease);
        return canIncrease;
    }


    // 방문 시간을 현재 시간으로 업데이트하는 메서드
    public void updateVisitedAt() {
        this.visitedAt = LocalDateTime.now();
    }
}
