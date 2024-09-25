package deepdivers.community.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.vo.CategoryStatus;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.encryptor.Encryptor;

@SpringBootTest
@Transactional
class PostServiceTest {

	@Autowired
	private deepdivers.community.domain.post.service.PostService postService;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private MemberRepository memberRepository;

	private PostCategory category;

	@Mock
	private Encryptor encryptor;

	private Member member;

	@BeforeEach
	void setUp() {
		encryptor = mock(Encryptor.class);

		when(encryptor.encrypt(anyString())).thenReturn("encryptedPassword");

		MemberSignUpRequest request = new MemberSignUpRequest(
			"test@mail.com",    // email
			"password123*",     // password (평문)
			"nickname",         // nickname
			"http://image.url", // image URL
			"010-1234-5678"     // phone number
		);

		member = Member.of(request, encryptor);

		memberRepository.save(member);  // memberRepository를 사용해 member를 저장

		category = PostCategory.createCategory("테스트 카테고리", "테스트 설명", CategoryStatus.ACTIVE);

		categoryRepository.save(category);
	}



	@Test
	@DisplayName("게시물 생성 성공 통합 테스트")
	void createPostSuccessIntegrationTest() {
		// Given
		PostCreateRequest request = new PostCreateRequest("통합 테스트 제목", "통합 테스트 내용", category.getId(), new String[]{"hashtag"});

		// When
		API<PostCreateResponse> response = postService.createPost(request, member);

		// Then
		assertThat(response).isNotNull();
		PostCreateResponse result = response.result();
		assertThat(result.postId()).isNotNull();  // postId 검증

		// DB에 저장된 게시물 검증
		Post savedPost = postRepository.findById(result.postId()).orElse(null);
		assertThat(savedPost).isNotNull();
		assertThat(savedPost.getTitle().getTitle()).isEqualTo("통합 테스트 제목");
		assertThat(savedPost.getContent().getContent()).isEqualTo("통합 테스트 내용");
	}


	@Test
	@DisplayName("존재하지 않는 카테고리로 게시물 생성 시 예외 발생 통합 테스트")
	void createPostWithInvalidCategoryIntegrationTest() {
		// Given
		PostCreateRequest request = new PostCreateRequest("유효한 제목", "유효한 내용", 999L, new String[]{"hashtag"});

		// When & Then
		assertThatThrownBy(() -> postService.createPost(request, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionType.CATEGORY_NOT_FOUND);
	}

	@Test
	@DisplayName("유효하지 않은 해시태그로 게시물 생성 시 예외 발생 통합 테스트")
	void createPostWithInvalidHashtagIntegrationTest() {
		// Given
		PostCreateRequest request = new PostCreateRequest("유효한 제목", "유효한 내용", category.getId(), new String[]{"invalid#hashtag"});

		// When & Then
		assertThatThrownBy(() -> postService.createPost(request, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", HashtagExceptionType.INVALID_HASHTAG_FORMAT);
	}
}
