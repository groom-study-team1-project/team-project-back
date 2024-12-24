package deepdivers.community.domain;


import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.comment.Comment;
import deepdivers.community.domain.post.repository.CommentRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.infra.aws.s3.S3TagManagerTest;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceTest extends S3TagManagerTest {

    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired EntityManager entityManager;

    protected Member getMember(Long id) {
        return memberRepository.findById(id).get();
    }

    protected Post getPost(Long id) {
        return postRepository.findById(id).get();
    }

    protected Comment getComment(Long id) {
        return commentRepository.findById(id).get();
    }

    protected void cacheClear() {
        entityManager.flush();
        entityManager.clear();
    }

}
