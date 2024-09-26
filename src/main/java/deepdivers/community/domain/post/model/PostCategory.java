package deepdivers.community.domain.post.model;

import java.util.List;

import deepdivers.community.domain.post.model.vo.CategoryStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
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
public class PostCategory {

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

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Post> posts;

    private PostCategory(String title, String description, CategoryStatus status) {
        this.title = title;
        this.description = description;
        if (status != null) {
            this.status = status;
        } else {
            this.status = CategoryStatus.ACTIVE;
        }
    }

    public static PostCategory createCategory(String title, String description, CategoryStatus status) {
        return new PostCategory(title, description, status);
    }

    public void deactivate() {
        this.status = CategoryStatus.INACTIVE;
    }

    public String getName() {
        return this.title;
    }
}
