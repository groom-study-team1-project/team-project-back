package deepdivers.community.domain.post.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "imageUrl")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String imageUrl;

    public PostImage(final Post post, final String imageUrl) {
        this.post = post;
        this.imageUrl = imageUrl;
    }
}