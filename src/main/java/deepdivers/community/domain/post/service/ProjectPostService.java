package deepdivers.community.domain.post.service;

import deepdivers.community.domain.category.entity.PostCategory;
import deepdivers.community.domain.category.service.CategoryService;
import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.domain.file.application.FileService;
import deepdivers.community.domain.file.repository.entity.FileType;
import deepdivers.community.domain.hashtag.service.HashtagService;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.domain.PostCreator;
import deepdivers.community.domain.post.domain.adaptor.ProjectPostAdaptor;
import deepdivers.community.domain.post.dto.code.PostStatusCode;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import deepdivers.community.domain.post.entity.Post;
import deepdivers.community.domain.post.entity.PostStatus;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import deepdivers.community.domain.post.repository.jpa.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectPostService {

    private final FileService fileService;
    private final CategoryService categoryService;
    private final HashtagService hashtagService;
    private final PostRepository postRepository;

    public API<Long> createProjectPost(final Member member, final ProjectPostRequest dto) {
        final PostCategory category = categoryService.getCategoryById(dto.categoryId());
        categoryService.validateProjectCategory(category);

        final PostCreator postCreator = new ProjectPostAdaptor(dto, category, member);
        final Post post = Post.of(postCreator);

        final Post savedPost = postRepository.save(post);
        hashtagService.createPostHashtags(post, dto.hashtags());
        fileService.createPostImage(dto.imageKeys(), savedPost.getId(), FileType.POST_CONTENT);
        fileService.createPostImage(dto.slideImageKeys(), savedPost.getId(), FileType.POST_SLIDE);

        return API.of(PostStatusCode.PROJECT_POST_CREATE_SUCCESS, savedPost.getId());
    }

    public API<Long> updateProjectPost(final Long postId, final Member member, final ProjectPostRequest dto) {
        final Post post = getPostByIdWithThrow(postId);
        validatePostAuthor(member, post);

        final PostCategory category = categoryService.getCategoryById(dto.categoryId());
        categoryService.validateProjectCategory(category);

        final PostCreator postCreator = new ProjectPostAdaptor(dto, category, member);
        final Post updatedPost = post.update(postCreator);

        hashtagService.updatePostHashtags(updatedPost, dto.hashtags());
        fileService.updatePostImage(dto.imageKeys(), postId, FileType.POST_CONTENT);
        fileService.updatePostImage(dto.slideImageKeys(), postId, FileType.POST_SLIDE);

        return API.of(PostStatusCode.PROJECT_POST_UPDATE_SUCCESS, postId);
    }

    public NoContent deletePost(final Long projectId, final Member member) {
        final Post post = getPostByIdWithThrow(projectId);

        validatePostAuthor(member, post);
        categoryService.validateProjectCategory(post.getCategory());

        post.deletePost();

        return NoContent.from(PostStatusCode.PROJECT_POST_DELETE_SUCCESS);
    }

    private Post getPostByIdWithThrow(final Long postId) {
        return postRepository.findByIdAndStatus(postId, PostStatus.ACTIVE)
            .orElseThrow(() -> new BadRequestException(PostExceptionCode.POST_NOT_FOUND));
    }

    private void validatePostAuthor(final Member member, final Post post) {
        if (!post.getMember().equals(member)) {
            throw new BadRequestException(PostExceptionCode.NOT_POST_AUTHOR);
        }
    }

}
