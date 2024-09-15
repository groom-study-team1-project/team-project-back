package deepdivers.community.domain.post.model;

import deepdivers.community.domain.post.model.vo.CategoryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "category",
    uniqueConstraints = @UniqueConstraint(columnNames = "title")
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 100)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryStatus status = CategoryStatus.ACTIVE;

    @Builder
    public Category(String title, String description, CategoryStatus status) {
        this.title = title;
        this.description = description;
        // null이 아닌 경우에만 status를 설정, 그렇지 않으면 기본값 유지
        this.status = (status != null) ? status : CategoryStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CategoryStatus.INACTIVE;
    }

    // title을 반환하도록 수정
    public String getName() {
        return this.title;
    }
}
