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
import deepdivers.community.domain.post.dto.response.*;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.*;
import deepdivers.community.domain.post.repository.*;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.uploader.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

	private final PostRepository postRepository;
	private final PostFileRepository postFileRepository;
	private final CategoryRepository categoryRepository;
	private final HashtagRepository hashtagRepository;
	private final PostHashtagRepository postHashtagRepository;
	private final PostVisitorRepository postVisitorRepository;
	private final CommentRepository commentRepository;
	private final PostQueryRepository postQueryRepository;
	private final JPAQueryFactory queryFactory;
	private final S3Uploader s3Uploader;

	public API<PostCreateResponse> createPost(PostCreateRequest request, Member member) {
		PostCategory postCategory = getCategoryById(request.categoryId());
		Post post = postRepository.save(Post.of(request, postCategory, member));

		List<PostFile> postFiles = request.imageUrls()
				.stream()
				.map(imageUrl -> {
					String[] splitResults = imageUrl.split("/temp/");
					String changedImageUrl = moveTempImageToPostBucket(splitResults[1], post.getId());
					return new PostFile(post, changedImageUrl);
				}).toList();

		postFileRepository.saveAll(postFiles);
		saveHashtags(post, request.hashtags());
		return API.of(PostStatusType.POST_CREATE_SUCCESS, PostCreateResponse.from(post));
	}

	public API<PostImageUploadResponse> postImageUpload(final MultipartFile imageFile) {
		final String uploadUrl = s3Uploader.postImageUpload(imageFile);
		return API.of(PostStatusType.POST_UPLOAD_IMAGE_SUCCESS, PostImageUploadResponse.of(uploadUrl));
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

	public API<PostUpdateResponse> updatePost(Long postId, PostUpdateRequest request, Member member) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));

		if (!post.getMember().getId().equals(member.getId())) {
			throw new BadRequestException(PostExceptionType.NOT_POST_AUTHOR);
		}

		PostCategory postCategory = getCategoryById(request.categoryId());

		post.updatePost(PostTitle.of(request.title()), PostContent.of(request.content()), postCategory);

		// removeExistingHashtags(postId);

		saveHashtags(post, request.hashtags());

		cleanUpUnusedHashtags();

		updatePostImages(post, request.imageUrls());

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
		removeExistingHashtags(postId);
		cleanUpCategory(post.getCategory());

		deleteVisitors(post);

		postRepository.delete(post);

		cleanUpUnusedHashtags();

		return NoContent.from(PostStatusType.POST_DELETE_SUCCESS);
	}

	private void deleteVisitors(Post post) {
		postVisitorRepository.deleteAllByPost(post);
	}


	private void removeExistingHashtags(Long postId) {
		List<PostHashtag> postHashtags = postHashtagRepository.findAllByPostId(postId);
		postHashtagRepository.deleteAll(postHashtags);
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

	private String moveTempImageToPostBucket(String fileName, Long postId) {
		String tempKey = String.format("temp/%s", fileName);
		String finalKey = String.format("posts/%d/%s", postId, fileName);

		return s3Uploader.moveImage(tempKey, finalKey);
	}

	private void updatePostImages(Post post, List<String> newImageUrls) {
		List<String> existingImageUrls = post.getPostFiles()
				.stream()
				.map(PostFile::getImageUrl)
				.toList();

		List<String> imagesToDelete = existingImageUrls.stream()
				.filter(url -> url.contains("/posts/") && !newImageUrls.contains(url))
				.toList();

		if(!imagesToDelete.isEmpty()){
			imagesToDelete.forEach(imageUrl -> postFileRepository.deleteByImageUrlAndPostId(imageUrl, post.getId()));
		}

		List<PostFile> postFilesToAdd = newImageUrls.stream()
			.filter(url -> url.contains("/temp/"))
			.map(imageUrl -> {
				String[] splitResults = imageUrl.split("/temp/");
				String changedImageUrl = moveTempImageToPostBucket(splitResults[1], post.getId());
				return new PostFile(post, changedImageUrl);
			})
			.toList();

		postFileRepository.saveAll(postFilesToAdd);
	}
}
