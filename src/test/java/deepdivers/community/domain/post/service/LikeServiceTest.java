package deepdivers.community.domain.post.service;

import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.model.PostContent;
import deepdivers.community.domain.post.model.PostTitle;
import deepdivers.community.domain.post.model.like.Like;
import deepdivers.community.domain.post.model.like.LikeId;
import deepdivers.community.domain.post.model.vo.CategoryStatus;
import deepdivers.community.domain.post.model.vo.LikeTarget;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.domain.post.repository.LikeRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.model.vo.PostStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.global.utility.encryptor.Encryptor;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    @DisplayName("게시물 좋아요 성공 테스트")
    void likePost_Success() {
        // When: 좋아요 요청 수행
        likeService.likePost(likeRequest, memberId);

        // Then: 해당 게시물에 좋아요가 되었는지 확인
        LikeId likeId = LikeId.of(postId, memberId, LikeTarget.POST);
        Like savedLike = likeRepository.findById(likeId).orElseThrow();

        assertEquals(likeId, savedLike.getId());
    }

    @Test
    @DisplayName("게시물 좋아요 취소 성공 테스트")
    void unlikePost_Success() {
        // Given: 이미 좋아요가 된 상태 설정
        likeService.likePost(likeRequest, memberId);

        // When: 좋아요 취소 요청 수행
        likeService.unlikePost(likeRequest, memberId);

        // Then: 좋아요가 취소 되었는지 확인
        LikeId likeId = LikeId.of(postId, memberId, LikeTarget.POST);
        boolean likeExists = likeRepository.existsById(likeId);

        assertEquals(false, likeExists);
    }
}


