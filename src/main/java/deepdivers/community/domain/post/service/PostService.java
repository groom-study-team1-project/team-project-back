package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.hashtag.service.HashtagService;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.vo.PostStatus;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final VisitorService visitorService;
    private final HashtagService hashtagService;

    public API<PostSaveResponse> createPost(final PostSaveRequest request, final Member member) {
        final PostCategory postCategory = categoryService.getCategoryById(request.categoryId());
        final Post post = Post.of(request, postCategory, member);

        final Set<PostHashtag> hashtags = hashtagService.connectPostWithHashtag(post, request.hashtags());
        final Post savedPost = postRepository.save(post.connectHashtags(hashtags));

        return API.of(PostStatusType.POST_CREATE_SUCCESS, PostSaveResponse.from(savedPost));
    }

    public API<PostSaveResponse> updatePost(final Long postId, final PostSaveRequest request, final Member member) {
        final PostCategory postCategory = categoryService.getCategoryById(request.categoryId());
        final Post post = getPostByMember(postId, member);
        post.updatePost(request, postCategory);

        final Set<PostHashtag> hashtags = hashtagService.connectPostWithHashtag(post, request.hashtags());
        final Post savedPost = postRepository.save(post.connectHashtags(hashtags));

        return API.of(PostStatusType.POST_UPDATE_SUCCESS, PostSaveResponse.from(savedPost));
    }

    public NoContent deletePost(final Long postId, final Member member) {
        final Post post = getPostByIdWithThrow(postId);
        validateAuthor(member, post);

        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);

        return NoContent.from(PostStatusType.POST_DELETE_SUCCESS);
    }

    @Transactional(readOnly = true)
    public PostReadResponse readPostDetail(final Long postId, final String ipAddr) {
        final Post post = getPostByIdWithThrow(postId);

        visitorService.increaseViewCount(post, ipAddr);

        return PostReadResponse.from(post);
    }

    private Post getPostByMember(final Long postId, final Member member) {
        return postRepository.findByIdAndMemberId(postId, member.getId())
                .orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));
    }

    private Post getPostByIdWithThrow(final Long postId) {
        return postRepository.findByIdAndStatus(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));
    }

    private static void validateAuthor(Member member, Post post) {
        if (!post.getMember().getId().equals(member.getId())) {
            throw new BadRequestException(PostExceptionType.NOT_POST_AUTHOR);
        }
    }

}
