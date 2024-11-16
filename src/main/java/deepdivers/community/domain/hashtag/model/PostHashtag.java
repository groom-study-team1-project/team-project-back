package deepdivers.community.domain.hashtag.model;

import deepdivers.community.domain.common.BaseEntity;
import deepdivers.community.domain.post.model.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false, of = {"post", "hashtag"}) // 중복 방지를 위해 equals와 hashCode 수정
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	indexes = {
		@Index(name = "idx_post_hashtag_post_id", columnList = "post_id"),
		@Index(name = "idx_post_hashtag_hashtag_id", columnList = "hashtag_id")
	}
)
public class PostHashtag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "hashtag_id")
	private Hashtag hashtag;

	@Builder
	public PostHashtag(Post post, Hashtag hashtag) {
		this.post = post;
		this.hashtag = hashtag;
	}

	public static PostHashtag of(final Post post, final Hashtag hashtag) {
		return new PostHashtag(post, hashtag);
	}

}

