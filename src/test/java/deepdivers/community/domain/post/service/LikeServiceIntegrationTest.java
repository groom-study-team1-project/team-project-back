package deepdivers.community.domain.post.service;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.LikeExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.PostContent;
import deepdivers.community.domain.post.model.PostTitle;
import deepdivers.community.domain.post.model.like.Like;
import deepdivers.community.domain.post.model.like.LikeId;
import deepdivers.community.domain.post.model.vo.CategoryStatus;
import deepdivers.community.domain.post.model.vo.LikeTarget;
import deepdivers.community.domain.post.model.vo.PostStatus;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.domain.post.repository.LikeRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.encryptor.Encryptor;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class LikeServiceIntegrationTest {

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
        memberId = setupTestMember().getId();
        postId = setupTestPost(setupTestCategory(), memberId).getId();
        likeRequest = new LikeRequest(postId);
    }

    private Member setupTestMember() {
        Encryptor encryptor = mock(Encryptor.class);
        when(encryptor.encrypt("testPassword123!")).thenReturn("encryptedPassword");

        MemberSignUpRequest signUpRequest = new MemberSignUpRequest(
                "testEmail@gmail.com", "testPassword123!", "testNickname", "http://testImage.url", "010-1234-5678"
        );

        return memberRepository.save(Member.of(signUpRequest, encryptor));
    }

    private PostCategory setupTestCategory() {
        return categoryRepository.save(PostCategory.createCategory(
                "test category", "test description", CategoryStatus.ACTIVE));
    }

    private Post setupTestPost(PostCategory category, Long memberId) {
        return postRepository.save(new Post(
                PostTitle.of("testTitle"),
                PostContent.of("testContent"),
                category,
                memberRepository.findById(memberId).orElseThrow(),
                deepdivers.community.domain.post.model.vo.PostStatus.ACTIVE
        ));
    }

    @Test
    @DisplayName("게시글 좋아요 기능이 데이터베이스에 반영되는지 검증")
    void likePost_SaveToDB() {
        // When: 좋아요 요청 수행
        NoContent response = likeService.likePost(likeRequest, memberId);

        // Then: 요청 성공 및 데이터베이스 반영 확인
        assertNotNull(response); // 응답 객체가 null이 아님을 확인
        assertTrue(likeRepository.existsById(LikeId.of(postId, memberId, LikeTarget.POST))); // 좋아요가 저장되었는지 확인
    }


    @Test
    @DisplayName("중복된 좋아요 요청 시 예외 발생 및 데이터베이스 변경 없음")
    void likePost_DuplicateThrows() {
        // Given: 좋아요 요청이 이미 수행된 상태
        likeService.likePost(likeRequest, memberId);

        // When & Then: 중복 요청 시 예외 발생 확인
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> likeService.likePost(likeRequest, memberId)
        );

        assertEquals(LikeExceptionType.INVALID_ACCESS, exception.getExceptionType());
    }

    @Test
    @DisplayName("게시글 좋아요 취소 시 데이터베이스에서 삭제되는지 확인")
    void unlikePost_DeleteFromDB() {
        // Given: 좋아요가 이미 추가된 상태
        likeService.likePost(likeRequest, memberId);

        // When: 좋아요 취소 요청 수행
        NoContent response = likeService.unlikePost(likeRequest, memberId);

        // Then: 요청 성공 및 데이터베이스 삭제 확인
        assertNotNull(response); // 응답 객체가 null이 아님을 확인
        assertFalse(likeRepository.existsById(LikeId.of(postId, memberId, LikeTarget.POST))); // 좋아요가 삭제되었는지 확인
    }

    @Test
    @DisplayName("중복된 좋아요 취소 요청 시 예외 발생 및 데이터베이스 변경 없음")
    void unlikePost_DuplicateThrows() {
        // Given: 좋아요가 이미 취소된 상태
        likeService.likePost(likeRequest, memberId);
        likeService.unlikePost(likeRequest, memberId);

        // When & Then: 중복 취소 요청 시 예외 발생 확인
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> likeService.unlikePost(likeRequest, memberId)
        );

        assertEquals(LikeExceptionType.INVALID_ACCESS, exception.getExceptionType());
    }
}
