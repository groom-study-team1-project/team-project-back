package deepdivers.community.domain.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
import deepdivers.community.domain.hashtag.model.Hashtag;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.hashtag.repository.HashtagRepository;
import deepdivers.community.domain.hashtag.repository.PostHashtagRepository;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.model.Category;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.vo.PostStatus;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final HashtagRepository hashtagRepository; // 해시태그 저장소 추가
	private final PostHashtagRepository postHashtagRepository; // 게시글-해시태그 관계 저장소 추가

	public API<PostCreateResponse> createPost(PostRequest request, Member member) {
		// 카테고리 조회
		Category category = getCategoryById(request.categoryId());

		// Post 엔티티 생성
		Post post = Post.builder()
			.title(request.title())
			.content(request.content())
			.category(category)
			.member(member)
			.status(PostStatus.ACTIVE)
			.build();

		// 게시글 저장
		postRepository.save(post);

		// 해시태그 저장 및 게시글-해시태그 관계 설정
		saveHashtags(post, request.hashtags());

		// API 응답 반환
		return API.of(PostStatusType.POST_CREATE_SUCCESS, PostCreateResponse.from(post));
	}

	private void saveHashtags(Post post, String[] hashtags) {
		if (hashtags == null || hashtags.length == 0) {
			return; // 해시태그가 없으면 바로 반환
		}

		for (String hashtagStr : hashtags) {
			if (hashtagStr == null || hashtagStr.isBlank() || !isValidHashtag(hashtagStr)) {
				throw new BadRequestException(HashtagExceptionType.INVALID_HASHTAG_FORMAT);
			}

			Hashtag hashtag = hashtagRepository.findByHashtag(hashtagStr)
				.orElseGet(() -> hashtagRepository.save(new Hashtag(hashtagStr))); // 해시태그가 없으면 새로 저장

			PostHashtag postHashtag = PostHashtag.builder()
				.post(post)
				.hashtag(hashtag)
				.build();

			postHashtagRepository.save(postHashtag); // 게시글-해시태그 관계 저장
		}
	}

	// 해시태그 유효성을 검사하는 메서드
	private boolean isValidHashtag(String hashtag) {
		// 해시태그는 '#'으로 시작하고, 1자 이상 10자 이하의 문자가 뒤에 붙어야 함
		return hashtag.matches("^#[\\w]{1,10}$");
	}



	private Category getCategoryById(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new BadRequestException(CategoryExceptionType.CATEGORY_NOT_FOUND));
	}
}
