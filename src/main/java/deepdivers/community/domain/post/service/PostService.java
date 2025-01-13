package deepdivers.community.domain.post.service;

import deepdivers.community.domain.category.entity.PostCategory;
import deepdivers.community.domain.category.service.CategoryService;
import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.common.exception.NotFoundException;
import deepdivers.community.domain.file.application.FileService;
import deepdivers.community.domain.file.repository.entity.FileType;
import deepdivers.community.domain.hashtag.service.HashtagService;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.domain.PostCreator;
import deepdivers.community.domain.post.domain.adaptor.GeneralPostAdaptor;
import deepdivers.community.domain.post.dto.code.PostStatusCode;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.post.entity.Post;
import deepdivers.community.domain.post.entity.PostStatus;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import deepdivers.community.domain.post.repository.jpa.PostRepository;
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
    private final FileService fileService;

    public API<PostSaveResponse> createPost(final PostSaveRequest request, final Member member) {
        final PostCategory category = categoryService.getCategoryById(request.categoryId());
        categoryService.validateGeneralCategory(category);

        final PostCreator postCreator = new GeneralPostAdaptor(request, category, member);
        final Post post = Post.of(postCreator);

        final Post savedPost = postRepository.save(post);
        hashtagService.createPostHashtags(post, request.hashtags());
        fileService.createPostImage(request.imageKeys(), savedPost.getId(), FileType.POST_CONTENT);

        return API.of(PostStatusCode.POST_CREATE_SUCCESS, PostSaveResponse.from(savedPost));
    }

    public API<PostSaveResponse> updatePost(final Long postId, final PostSaveRequest request, final Member member) {
        final Post post = getPostByIdWithThrow(postId);
        validatePostAuthor(member, post);

        final PostCategory category = categoryService.getCategoryById(request.categoryId());
        categoryService.validateGeneralCategory(category);

        final PostCreator postCreator = new GeneralPostAdaptor(request, category, member);
        final Post updatedPost = post.update(postCreator);

        hashtagService.updatePostHashtags(updatedPost, request.hashtags());
        fileService.updatePostImage(request.imageKeys(), updatedPost.getId(), FileType.POST_CONTENT);

        return API.of(PostStatusCode.POST_UPDATE_SUCCESS, PostSaveResponse.from(updatedPost));
    }

    public NoContent deletePost(final Long postId, final Member member) {
        final Post post = getPostByIdWithThrow(postId);

        validatePostAuthor(member, post);
        categoryService.validateGeneralCategory(post.getCategory());

        post.deletePost();

        return NoContent.from(PostStatusCode.POST_DELETE_SUCCESS);
    }

    private Post getPostByIdWithThrow(final Long postId) {
        return postRepository.findByIdAndStatus(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(PostExceptionCode.POST_NOT_FOUND));
    }

    private void validatePostAuthor(final Member member, final Post post) {
        if (!post.getMember().equals(member)) {
            throw new BadRequestException(PostExceptionCode.NOT_POST_AUTHOR);
        }
    }

}
