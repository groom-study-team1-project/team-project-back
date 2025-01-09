package deepdivers.community.domain.hashtag.entity;

import deepdivers.community.domain.hashtag.exception.HashtagExceptionCode;
import deepdivers.community.domain.common.exception.BadRequestException;
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
@EqualsAndHashCode(callSuper = false, of = {"id", "hashtag"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	uniqueConstraints = @UniqueConstraint(
		name = "uk_hashtag_hashtag",
		columnNames = {"hashtag"}
	)
)
public class Hashtag {

	private static final String HASHTAG_REG_EXP = "^[\\p{L}\\p{N}]{1,10}$";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String hashtag;

	protected Hashtag(final String hashtag) {
		validate(hashtag);
		this.hashtag = hashtag;
	}

	public static Hashtag from(final String hashtag) {
		return new Hashtag(hashtag);
	}

	public static void validate(String hashtag) {
		validateHashtagNullOrBlank(hashtag);
		validateHashTagFormat(hashtag);
	}

	private static void validateHashTagFormat(String hashtag) {
		if (!hashtag.matches(HASHTAG_REG_EXP)) {
			throw new BadRequestException(HashtagExceptionCode.INVALID_HASHTAG_FORMAT);
		}
	}

	private static void validateHashtagNullOrBlank(String hashtag) {
		if (hashtag == null || hashtag.isBlank()) {
			throw new BadRequestException(HashtagExceptionCode.INVALID_HASHTAG_FORMAT);
		}
	}

}
