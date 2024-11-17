package deepdivers.community.domain.post.service;

import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostVisitor;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.domain.post.repository.PostVisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final PostVisitorRepository postVisitorRepository;
    private final PostRepository postRepository;

    public void increaseViewCount(Post post, String ipAddr) {
        PostVisitor postVisitor = postVisitorRepository.findByPostAndIpAddr(post, ipAddr)
                .orElseGet(() -> createNewPostVisitor(post, ipAddr));

        if (postVisitor.canIncreaseViewCount()) {
            post.increaseViewCount();
            postVisitor.updateVisitedAt();
            postVisitorRepository.save(postVisitor);
            postRepository.save(post);
        }
    }

    private PostVisitor createNewPostVisitor(Post post, String ipAddr) {
        PostVisitor newVisitor = new PostVisitor(post, ipAddr);
        post.increaseViewCount();
        postVisitorRepository.save(newVisitor);
        postRepository.save(post);
        return newVisitor;
    }

    public void deleteVisitorsByPostId(Long postId) {
        postVisitorRepository.deleteAllByPostId(postId);
    }
}
