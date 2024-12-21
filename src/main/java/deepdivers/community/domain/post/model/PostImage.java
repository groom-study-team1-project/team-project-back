package deepdivers.community.domain.post.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "imageKey")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String imageKey;

    protected PostImage(final Post post, final String imageKey) {
        this.post = post;
        this.imageKey = imageKey;
    }

    public static PostImage of(final Post post, final String imageKey) {return new PostImage(post, imageKey);}

}