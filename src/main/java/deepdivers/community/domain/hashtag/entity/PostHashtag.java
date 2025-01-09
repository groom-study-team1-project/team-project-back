package deepdivers.community.domain.hashtag.entity;

import deepdivers.community.domain.common.entity.TimeBaseEntity;
import deepdivers.community.domain.post.entity.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false, of = {"post", "hashtag"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	indexes = {
		@Index(name = "idx_post_hashtag_post_id", columnList = "post_id"),
		@Index(name = "idx_post_hashtag_hashtag_id", columnList = "hashtag_id")
	}
)
public class PostHashtag extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hashtag_id")
	private Hashtag hashtag;

	protected PostHashtag(final Post post, final Hashtag hashtag) {
		this.post = post;
		this.hashtag = hashtag;
	}

	public static PostHashtag of(final Post post, final Hashtag hashtag) {
		return new PostHashtag(post, hashtag);
	}

}

