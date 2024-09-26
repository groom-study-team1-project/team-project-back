// package deepdivers.community.domain.post.model.hashtag;
//
// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import jakarta.persistence.UniqueConstraint;
// import lombok.AccessLevel;
// import lombok.EqualsAndHashCode;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import deepdivers.community.global.exception.model.BadRequestException;
// import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
//
// @Entity
// @Getter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// @Table(
// 	uniqueConstraints = @UniqueConstraint(
// 		name = "uk_hashtag_hashtag",
// 		columnNames = {"hashtag"}
// 	)
// )
// public class Hashtag {
//
// 	@Id
// 	@GeneratedValue(strategy = GenerationType.IDENTITY)
// 	private Long id;
//
// 	@Column(nullable = false, length = 50)
// 	private String hashtag;
//
// 	public Hashtag(String hashtag) {
// 		validate(hashtag); // 생성 시 검증
// 		this.hashtag = hashtag;
// 	}
//
// 	public static String validate(String hashtag) {
// 		if (hashtag == null || hashtag.isBlank()) {
// 			throw new BadRequestException(HashtagExceptionType.INVALID_HASHTAG_FORMAT);
// 		}
// 		return hashtag;
// 	}
//
// 	public String getName() {
// 		return hashtag;
// 	}
// }
