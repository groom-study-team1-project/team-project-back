package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.hashtag.service.HashtagService;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.response.PostImageUploadResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.vo.PostStatus;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.infra.aws.s3.S3TagManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final VisitorService visitorService;
    private final HashtagService hashtagService;
    private final S3TagManager s3TagManager;

    public API<PostSaveResponse> createPost(final PostSaveRequest request, final Member member) {
        final PostCategory postCategory = categoryService.getCategoryById(request.categoryId());
        final Post post = Post.of(request, postCategory, member);

        final Set<PostHashtag> hashtags = hashtagService.createPostHashtags(post, request.hashtags());
        final Post savedPost = createPost(post, hashtags, request.imageKeys());
        request.imageKeys().forEach(s3TagManager::removeDeleteTag);

        return API.of(PostStatusType.POST_CREATE_SUCCESS, PostSaveResponse.from(savedPost));
    }

    public API<PostSaveResponse> updatePost(final Long postId, final PostSaveRequest request, final Member member) {
        final PostCategory postCategory = categoryService.getCategoryById(request.categoryId());
        final Post post = getPostByIdWithThrow(postId);
        validatePostAuthor(member, post);

        final Post updatedPost = updatePost(request, post, postCategory);
        post.getImageKeys().forEach(s3TagManager::markAsDeleted);
        request.imageKeys().forEach(s3TagManager::removeDeleteTag);

        return API.of(PostStatusType.POST_UPDATE_SUCCESS, PostSaveResponse.from(updatedPost));
    }

    private Post updatePost(final PostSaveRequest request, final Post post, final PostCategory postCategory) {
        return postRepository.save(post.updatePost(request, postCategory)
            .connectHashtags(hashtagService.updatePostHashtags(post, request.hashtags()))
            .connectImageKey(request.imageKeys())
        );
    }

    public NoContent deletePost(final Long postId, final Member member) {
        final Post post = getPostByIdWithThrow(postId);
        validatePostAuthor(member, post);

        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);

        return NoContent.from(PostStatusType.POST_DELETE_SUCCESS);
    }

    @Transactional(readOnly = true)
    public API<PostReadResponse> readPostDetail(final Long postId, final String ipAddr) {
        final Post post = getPostByIdWithThrow(postId);
        visitorService.increaseViewCount(post, ipAddr);

        return API.of(
                PostStatusType.POST_VIEW_SUCCESS,
                PostReadResponse.from(post)
        );
    }

    public Post createPost(final Post post, final Set<PostHashtag> hashtags, final List<String> imageKeys) {
        return postRepository.save(
            post.connectHashtags(hashtags)
            .connectImageKey(imageKeys)
        );
    }

    private Post getPostByIdWithThrow(final Long postId) {
        return postRepository.findByIdAndStatus(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));
    }

    private void validatePostAuthor(final Member member, final Post post) {
        if (!post.getMember().equals(member)) {
            throw new BadRequestException(PostExceptionType.NOT_POST_AUTHOR);
        }
    }

}
