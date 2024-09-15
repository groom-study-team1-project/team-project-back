package deepdivers.community.domain.hashtag.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	uniqueConstraints = @UniqueConstraint(
		name = "uk_hashtag_hashtag",
		columnNames = {"hashtag"}
	)
)
public class Hashtag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String hashtag;

	public Hashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public String getName() {
		return hashtag;
	}
}
