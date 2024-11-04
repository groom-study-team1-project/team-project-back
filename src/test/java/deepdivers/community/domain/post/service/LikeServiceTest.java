package deepdivers.community.domain.post.service;

import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.model.PostContent;
import deepdivers.community.domain.post.model.PostTitle;
import deepdivers.community.domain.post.model.vo.CategoryStatus;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.domain.post.repository.LikeRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.model.vo.PostStatus;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.global.utility.encryptor.Encryptor;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@Transactional
class LikeServiceTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long memberId;
    private Long postId;
    private LikeRequest likeRequest;
    private Post post;
    private Member member;
    private PostCategory category;

    @BeforeEach
    void setUp() {
        // Mock 객체 설정
        Encryptor encryptor = Mockito.mock(Encryptor.class);
        Mockito.when(encryptor.encrypt(anyString())).thenReturn("encryptedPassword");

        // Member 생성 및 저장
        MemberSignUpRequest request = new MemberSignUpRequest(
                "testEmail@gmail.com",
                "testPassword123!",
                "testNickname",
                "http://testImage.url",
                "010-1234-5678"
        );
        member = memberRepository.save(Member.of(request, encryptor));
        memberId = member.getId();

        // 카테고리 생성 및 저장
        category = categoryRepository.save(PostCategory.createCategory("test category", "test description", CategoryStatus.ACTIVE));

        // 게시물 생성 및 저장
        post = postRepository.save(new Post(
                PostTitle.of("testTitle"),
                PostContent.of("testContent"),
                category,
                member,
                PostStatus.ACTIVE
        ));
        postId = post.getId();

        // LikeRequest 초기화
        likeRequest = new LikeRequest(postId);
    }

}