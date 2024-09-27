package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
import deepdivers.community.domain.hashtag.model.Hashtag;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.hashtag.repository.HashtagRepository;
import deepdivers.community.domain.hashtag.repository.PostHashtagRepository;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.request.PostUpdateRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.PostUpdateResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.PostContent;
import deepdivers.community.domain.post.model.PostTitle;
import deepdivers.community.domain.post.model.PostVisitor;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.domain.post.repository.PostVisitorRepository;
import deepdivers.community.domain.global.exception.model.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final HashtagRepository hashtagRepository;
	private final PostHashtagRepository postHashtagRepository;
	private final PostVisitorRepository postVisitorRepository;

	public API<PostCreateResponse> createPost(PostCreateRequest request, Member member) {
		PostCategory postCategory = getCategoryById(request.categoryId());
		Post post = Post.of(request, postCategory, member);
		postRepository.save(post);
		saveHashtags(post, request.hashtags());
		return API.of(PostStatusType.POST_CREATE_SUCCESS, PostCreateResponse.from(post));
	}

	@Transactional(readOnly = true)
	public List<PostReadResponse> getAllPosts() {
		List<Post> posts = postRepository.findAll();
		return posts.stream()
			.map(PostReadResponse::from)
			.collect(Collectors.toList());
	}

	@Transactional
	public PostReadResponse getPostById(Long postId, String ipAddr) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));
		increaseViewCount(post, ipAddr);
		return PostReadResponse.from(post);
	}

	@Transactional
	public API<PostUpdateResponse> updatePost(Long postId, PostUpdateRequest request, Member member) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));

		// 작성자 확인
		if (!post.getMember().getId().equals(member.getId())) {
			throw new BadRequestException(PostExceptionType.NOT_POST_AUTHOR);
		}

		PostCategory postCategory = getCategoryById(request.categoryId());

		// 게시글 수정
		post.updatePost(PostTitle.of(request.title()), PostContent.of(request.content()), postCategory);

		saveHashtags(post, request.hashtags());

		postRepository.save(post);

		return API.of(PostStatusType.POST_UPDATE_SUCCESS, PostUpdateResponse.from(post));
	}

	private void saveHashtags(Post post, String[] hashtags) {
		if (hashtags == null || hashtags.length == 0) return;

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
		return hashtag.matches("^[\\p{L}\\p{N}]{1,10}$");
	}

	private PostCategory getCategoryById(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new BadRequestException(CategoryExceptionType.CATEGORY_NOT_FOUND));
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
