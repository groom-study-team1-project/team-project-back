package deepdivers.community.domain.post.service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.PostVisitor;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.domain.post.repository.PostVisitorRepository;
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
		PostCategory postCategory = getCategoryById(request.categoryId());

		Post post = Post.of(request, postCategory, member);

		postRepository.save(post);

		saveHashtags(post, request.hashtags());

		return API.of(PostStatusType.POST_CREATE_SUCCESS, PostCreateResponse.from(post));
	}

	private void saveHashtags(Post post, String[] hashtags) {
		if (hashtags == null || hashtags.length == 0) {
			return;
		}
		// 유효하지 않은 해시태그가 있으면 예외 발생
		Arrays.stream(hashtags)
			.filter(hashtag -> !isValidHashtag(hashtag))
			.findFirst()
			.ifPresent(invalidHashtag -> {
				throw new BadRequestException(HashtagExceptionType.INVALID_HASHTAG_FORMAT);
			});

		Set<PostHashtag> postHashtags = Arrays.stream(hashtags)
			.filter(this::isValidHashtag)
			.map(Hashtag::validate)
			.map(hashtag -> hashtagRepository.findByHashtag(hashtag)
				.orElseGet(() -> hashtagRepository.save(new Hashtag(hashtag))))
			.map(hashtag -> PostHashtag.builder()
				.post(post)
				.hashtag(hashtag)
				.build())
			.collect(Collectors.toSet());

		postHashtagRepository.saveAll(postHashtags);
	}


	private boolean isValidHashtag(String hashtag) {
		// 해시태그가 1자 이상 10자 이하의 문자로만 구성되었는지 확인
		return hashtag.matches("^[\\p{L}\\p{N}]{1,10}$");
	}

	private PostCategory getCategoryById(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new BadRequestException(CategoryExceptionType.CATEGORY_NOT_FOUND));
	}

	private final PostVisitorRepository postVisitorRepository;

	@Transactional
	public PostReadResponse getPostById(Long postId, String ipAddr) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));

		increaseViewCount(post, ipAddr);

		return PostReadResponse.from(post);
	}

	private void increaseViewCount(Post post, String ipAddr) {
		PostVisitor postVisitor = postVisitorRepository.findByPostAndIpAddr(post, ipAddr)
			.orElse(null);
		if (postVisitor == null) {
			PostVisitor newVisitor = new PostVisitor(post, ipAddr);
			post.increaseViewCount();
			postVisitorRepository.save(newVisitor);
			postRepository.save(post);
		} else if (postVisitor.canIncreaseViewCount()) {
			post.increaseViewCount();
			postVisitor.updateVisitedAt();
			postVisitorRepository.save(postVisitor);
			postRepository.save(post);
		}
	}

}
