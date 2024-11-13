package deepdivers.community.domain.post.service;

import static deepdivers.community.domain.post.model.QPost.*;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
import deepdivers.community.domain.hashtag.model.Hashtag;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.hashtag.repository.HashtagRepository;
import deepdivers.community.domain.hashtag.repository.PostHashtagRepository;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.request.PostUpdateRequest;
import deepdivers.community.domain.post.dto.response.PostAllReadResponse;
import deepdivers.community.domain.post.dto.response.PostCountResponse;
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
import deepdivers.community.domain.post.repository.CommentRepository;
import deepdivers.community.domain.post.repository.PostQueryRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.domain.post.repository.PostVisitorRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final HashtagRepository hashtagRepository;
	private final PostHashtagRepository postHashtagRepository;
	private final PostVisitorRepository postVisitorRepository;
	private final CommentRepository commentRepository;
	private final PostQueryRepository postQueryRepository;
	private final JPAQueryFactory queryFactory;


	public API<PostCreateResponse> createPost(final PostCreateRequest request, final Member member) {
		final PostCategory postCategory = getCategoryById(request.categoryId());
		final Post post = Post.of(request, postCategory, member);

		post.setPostHashtags(request.hashtags(), hashtagRepository);
		postRepository.save(post);

		return API.of(PostStatusType.POST_CREATE_SUCCESS, PostCreateResponse.from(post));
	}

	@Transactional(readOnly = true)
	public API<PostCountResponse> getAllPosts(Long lastContentId, Long categoryId) {

		Long totalPostCount = getTotalPostCount(categoryId);

		List<PostAllReadResponse> posts = postQueryRepository.findAllPosts(lastContentId, categoryId);

		PostCountResponse response = new PostCountResponse(totalPostCount, posts);
		return API.of(PostStatusType.POST_VIEW_SUCCESS, response);
	}

	private Long getTotalPostCount(Long categoryId) {
		BooleanBuilder whereClause = buildWhereClause(categoryId);

		Long totalPostCount = queryFactory.select(post.count())
			.from(post)
			.where(whereClause)
			.fetchOne();

		return totalPostCount != null ? totalPostCount : 0L;
	}

	private BooleanBuilder buildWhereClause(Long categoryId) {
		BooleanBuilder whereClause = new BooleanBuilder();
		if (categoryId != null) {
			whereClause.and(post.category.id.eq(categoryId));
		}
		return whereClause;
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

		if (!post.getMember().getId().equals(member.getId())) {
			throw new BadRequestException(PostExceptionType.NOT_POST_AUTHOR);
		}

		PostCategory postCategory = getCategoryById(request.categoryId());

		post.updatePost(PostTitle.of(request.title()), PostContent.of(request.content()), postCategory);

		removeExistingHashtags(post);

		//saveHashtags(post, request.hashtags());

		cleanUpUnusedHashtags();

		postRepository.save(post);

		return API.of(PostStatusType.POST_UPDATE_SUCCESS, PostUpdateResponse.from(post));
	}

	public NoContent deletePost(Long postId, Member member) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));

		if (!post.getMember().getId().equals(member.getId())) {
			throw new BadRequestException(PostExceptionType.NOT_POST_AUTHOR);
		}

		deleteComments(post);
		removeExistingHashtags(post);
		cleanUpCategory(post.getCategory());

		deleteVisitors(post);

		postRepository.delete(post);

		cleanUpUnusedHashtags();

		return NoContent.from(PostStatusType.POST_DELETE_SUCCESS);
	}

	private void deleteVisitors(Post post) {
		postVisitorRepository.deleteAllByPost(post);
	}


	private void removeExistingHashtags(Post post) {
		postHashtagRepository.deleteAllByPost(post);
	}

	private void deleteComments(Post post) {
		commentRepository.deleteAllByPost(post);
	}

	private void cleanUpCategory(PostCategory category) {
		if (postRepository.findByCategoryId(category.getId()).isEmpty()) {
			category.deactivate();
			categoryRepository.save(category);
		}
	}

	private void cleanUpUnusedHashtags() {
		List<Hashtag> unusedHashtags = hashtagRepository.findUnusedHashtags();
		if (!unusedHashtags.isEmpty()) {
			hashtagRepository.deleteAll(unusedHashtags);
		}
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
