package deepdivers.community.domain.category.entity;

import deepdivers.community.domain.category.exception.CategoryExceptionCode;
import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.post.domain.PostCreator;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "deepdive_commnuity_post_category",
    uniqueConstraints = @UniqueConstraint(columnNames = "title")
)
public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 100)
    private String description;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CategoryType categoryType;

    public PostCategory(String title, String description, CategoryType categoryType) {
        this.title = title;
        this.description = description;
        this.categoryType = categoryType;
    }

    public boolean isGeneralCategory() {
        return categoryType == CategoryType.GENERAL;
    }

    public boolean isProjectCategory() {
        return categoryType == CategoryType.PROJECT;
    }

    public boolean isSameCategoryType(final PostCategory category) {
        return categoryType == category.getCategoryType();
    }

}
