package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.hashtag.service.HashtagService;
import deepdivers.community.domain.image.application.ImageService;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.post.dto.code.PostStatusType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.entity.Post;
import deepdivers.community.domain.post.entity.PostCategory;
import deepdivers.community.domain.post.entity.PostStatus;
import deepdivers.community.domain.post.repository.jpa.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final HashtagService hashtagService;
    private final ImageService imageService;

    public API<PostSaveResponse> createPost(final PostSaveRequest request, final Member member) {
        final PostCategory postCategory = categoryService.getCategoryById(request.categoryId());
        final Post post = Post.of(request, postCategory, member);

        final Post savedPost = postRepository.save(post);
        hashtagService.createPostHashtags(post, request.hashtags());
        imageService.createPostContentImage(request.imageKeys(), savedPost.getId());

        return API.of(PostStatusType.POST_CREATE_SUCCESS, PostSaveResponse.from(savedPost));
    }

    public API<PostSaveResponse> updatePost(final Long postId, final PostSaveRequest request, final Member member) {
        final PostCategory postCategory = categoryService.getCategoryById(request.categoryId());
        final Post post = getPostByIdWithThrow(postId);
        validatePostAuthor(member, post);

        final Post updatedPost = post.updatePost(request, postCategory);
        hashtagService.updatePostHashtags(updatedPost, request.hashtags());
        imageService.updatePostContentImage(request.imageKeys(), updatedPost.getId());

        return API.of(PostStatusType.POST_UPDATE_SUCCESS, PostSaveResponse.from(updatedPost));
    }

    public NoContent deletePost(final Long postId, final Member member) {
        final Post post = getPostByIdWithThrow(postId);
        validatePostAuthor(member, post);

        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);

        return NoContent.from(PostStatusType.POST_DELETE_SUCCESS);
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
