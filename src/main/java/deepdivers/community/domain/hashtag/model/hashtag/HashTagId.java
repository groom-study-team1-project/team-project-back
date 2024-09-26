// package deepdivers.community.domain.post.model.hashtag;
//
// import deepdivers.community.domain.post.model.vo.HashTagTarget;
// import jakarta.persistence.Column;
// import jakarta.persistence.Embeddable;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import lombok.AccessLevel;
// import lombok.AllArgsConstructor;
// import lombok.NoArgsConstructor;
//
// @Embeddable
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// @AllArgsConstructor(access = AccessLevel.PROTECTED)
// public class HashTagId {
//
//     @Column(nullable = false)
//     private Long targetId;
//
//     @Column(nullable = false)
//     private Long hashTagId;
//
//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false, columnDefinition = "varchar(50)")
//     private HashTagTarget targetType;
//
// }
