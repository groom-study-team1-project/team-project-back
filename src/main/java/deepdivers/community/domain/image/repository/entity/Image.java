package deepdivers.community.domain.image.repository.entity;

import deepdivers.community.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "deepdive_community_image")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String imageKey;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    @Column(nullable = false)
    private Long referenceId;

    protected Image(String imageKey, String imageUrl, ImageType imageType, Long referenceId) {
        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.imageType = imageType;
        this.referenceId = referenceId;
    }

    public static Image createPostContentImage(final String imageKey, final String imageUrl, final Long postId) {
        return new Image(imageKey, imageUrl, ImageType.POST_CONTENT, postId);
    }

}
