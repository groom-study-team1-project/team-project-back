package deepdivers.community.domain.post.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.hashtag.repository.HashtagRepository;
import deepdivers.community.domain.hashtag.repository.PostHashtagRepository;
import deepdivers.community.domain.hashtag.service.HashtagService;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.response.*;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.*;
import deepdivers.community.domain.post.repository.*;
import deepdivers.community.global.exception.model.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static deepdivers.community.domain.post.model.QPost.post;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final PostHashtagRepository postHashtagRepository;
	private final PostVisitorRepository postVisitorRepository;
	private final CommentRepository commentRepository;
	private final HashtagService hashtagService;


	public API<PostCreateResponse> createPost(final PostCreateRequest request, final Member member) {
		final Post post = createOrUpdatePost(request, member, null);
		return API.of(PostStatusType.POST_CREATE_SUCCESS, PostCreateResponse.from(post));
	}

	@Transactional
	public API<PostUpdateResponse> updatePost(Long postId, final PostCreateRequest request, final Member member) {
		final Post post = postRepository.findByIdAndMemberId(postId, member.getId())
				.orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));

		final Post updatedPost = createOrUpdatePost(request, member, post);
		return API.of(PostStatusType.POST_UPDATE_SUCCESS, PostUpdateResponse.from(updatedPost));
	}

	@Transactional
	public PostReadResponse getPostById(Long postId, String ipAddr) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));
		increaseViewCount(post, ipAddr);
		return PostReadResponse.from(post);
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

	private Post createOrUpdatePost(final PostCreateRequest request, final Member member, final Post existingPost) {
		final PostCategory postCategory = getCategoryById(request.categoryId());
		final Post post;

		if (existingPost == null) {
			post = Post.of(request, postCategory, member);
		} else {
			existingPost.updatePost(
					PostTitle.of(request.title()),
					PostContent.of(request.content()),
					postCategory
			);
			post = existingPost;
		}

		final Set<PostHashtag> hashtags = hashtagService.connectPostWithHashtag(post, request.hashtags());
		return postRepository.save(post.connectHashtags(hashtags));
	}
}
