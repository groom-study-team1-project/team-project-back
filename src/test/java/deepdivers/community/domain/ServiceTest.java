package deepdivers.community.domain;


import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.infra.aws.s3.S3TagManagerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DirtiesContext
@Transactional
@Import(LocalStackTestConfig.class)
public class ServiceTest extends S3TagManagerTest {

    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;

    protected Member getMember(Long id) {
        return memberRepository.findById(id).get();
    }

    protected Post getPost(Long id) {
        return postRepository.findById(id).get();
    }

}
