package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.hashtag.service.HashtagService;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.PostUpdateResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.PostContent;
import deepdivers.community.domain.post.model.PostTitle;
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
    private final CommentService commentService;
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
        visitorService.increaseViewCount(post, ipAddr);

        return PostReadResponse.from(post);
    }

    public NoContent deletePost(Long postId, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(PostExceptionType.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(member.getId())) {
            throw new BadRequestException(PostExceptionType.NOT_POST_AUTHOR);
        }

        commentService.removeAllCommentsByPostId(postId);
        visitorService.deleteVisitorsByPostId(postId);

        postRepository.delete(post);

        return NoContent.from(PostStatusType.POST_DELETE_SUCCESS);
    }


    private Post createOrUpdatePost(final PostCreateRequest request, final Member member, final Post existingPost) {
        final PostCategory postCategory = categoryService.getCategoryById(request.categoryId());
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
