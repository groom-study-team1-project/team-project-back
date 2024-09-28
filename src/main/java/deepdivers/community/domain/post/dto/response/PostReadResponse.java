package deepdivers.community.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import deepdivers.community.domain.post.model.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "게시글 조회 응답")
@JsonIgnoreProperties(ignoreUnknown = true) // 불필요한 필드 무시
public record PostReadResponse(
	Long postId,
	String title,
	String content,
	Long categoryId, // 카테고리 ID 사용
	MemberInfo memberInfo, // 작성자 정보 포함
	CountInfo countInfo, // CountInfo 사용
	List<String> hashtags, // 해시태그 포함
	String createdAt // 생성일 포함
) {
	public static PostReadResponse from(Post post) {
		return new PostReadResponse(
			post.getId(),
			post.getTitle().getTitle(),
			post.getContent().getContent(),
			post.getCategory().getId(), // 카테고리 ID 가져오기
			new MemberInfo(
				post.getMember().getId(), // 작성자 ID
				post.getMember().getNickname(), // 작성자 닉네임
				post.getMember().getImageUrl() // 작성자 이미지 URL
			),
			new CountInfo(
				post.getViewCount(), // 조회수
				post.getLikeCount(), // 좋아요 수
				post.getCommentCount() // 댓글 수
			),
			post.getHashtags(), // 해시태그 리스트
			post.getCreatedAt().toString() // 생성일 포맷
		);
	}
}
