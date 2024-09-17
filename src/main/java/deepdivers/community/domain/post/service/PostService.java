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
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.PostContent;
import deepdivers.community.domain.post.model.vo.PostStatus;
import deepdivers.community.domain.post.model.PostTitle;
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
	private final HashtagRepository hashtagRepository;
	private final PostHashtagRepository postHashtagRepository;

	public API<PostCreateResponse> createPost(PostCreateRequest request, Member member) {
		// 카테고리 조회
		PostCategory postCategory = getCategoryById(request.categoryId());

		// Post 엔티티 생성
		Post post = Post.builder()
			.title(PostTitle.of(request.title()))
			.content(PostContent.of(request.content()))
			.category(postCategory)
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
				// 유효하지 않은 해시태그 형식일 경우 예외 발생
				throw new BadRequestException(HashtagExceptionType.INVALID_HASHTAG_FORMAT);
			}

			Hashtag hashtag = hashtagRepository.findByHashtag(hashtagStr)
				.orElseGet(() -> hashtagRepository.save(new Hashtag(hashtagStr)));

			PostHashtag postHashtag = PostHashtag.builder()
				.post(post)
				.hashtag(hashtag)
				.build();

			postHashtagRepository.save(postHashtag);
		}
	}

	private boolean isValidHashtag(String hashtag) {
		// 해시태그는 '#'으로 시작하고, 1자 이상 10자 이하의 알파벳, 숫자 또는 유니코드 문자(한국어 포함)로 구성되어야 함
		return hashtag.matches("^#[\\p{L}\\p{N}]{1,10}$");
	}

	private PostCategory getCategoryById(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new BadRequestException(CategoryExceptionType.CATEGORY_NOT_FOUND));
	}
}
