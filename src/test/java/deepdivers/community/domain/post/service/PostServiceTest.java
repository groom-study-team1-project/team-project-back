package deepdivers.community.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

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
import deepdivers.community.domain.post.dto.request.PostUpdateRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.PostUpdateResponse;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.vo.CategoryStatus;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.domain.global.exception.model.BadRequestException;
import deepdivers.community.domain.global.utility.encryptor.Encryptor;

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
		postRepository.deleteAll(); // 테스트 전에 모든 게시글 삭제
		categoryRepository.deleteAll(); // 모든 카테고리 삭제
		memberRepository.deleteAll(); // 모든 멤버 삭제

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

	@Test
	@DisplayName("게시글 조회 성공 통합 테스트")
	void getPostByIdSuccessIntegrationTest() {
		// Given
		PostCreateRequest createRequest = new PostCreateRequest("조회 테스트 제목", "조회 테스트 내용", category.getId(), new String[]{"hashtag"});
		API<PostCreateResponse> createResponse = postService.createPost(createRequest, member);
		Long postId = createResponse.result().postId(); // 생성된 게시물 ID

		// When
		PostReadResponse readResponse = postService.getPostById(postId, "127.0.0.1"); // IP 주소는 임의로 설정

		// Then
		assertThat(readResponse).isNotNull();
		assertThat(readResponse.postId()).isEqualTo(postId);
		assertThat(readResponse.title()).isEqualTo("조회 테스트 제목");
		assertThat(readResponse.content()).isEqualTo("조회 테스트 내용");
	}

	@Test
	@DisplayName("전체 게시글 조회 성공 통합 테스트")
	void getAllPostsSuccessIntegrationTest() {
		// Given
		PostCreateRequest request1 = new PostCreateRequest("첫 번째 게시글", "첫 번째 게시글 내용", category.getId(), new String[]{"hashtag1"});
		PostCreateRequest request2 = new PostCreateRequest("두 번째 게시글", "두 번째 게시글 내용", category.getId(), new String[]{"hashtag2"});
		postService.createPost(request1, member);
		postService.createPost(request2, member);

		// When
		List<PostReadResponse> response = postService.getAllPosts();

		// Then
		assertThat(response).hasSize(2);  // 게시글 2개가 존재하는지 확인
		assertThat(response.get(0).title()).isEqualTo("첫 번째 게시글");
		assertThat(response.get(0).content()).isEqualTo("첫 번째 게시글 내용");
		assertThat(response.get(1).title()).isEqualTo("두 번째 게시글");
		assertThat(response.get(1).content()).isEqualTo("두 번째 게시글 내용");
	}

	@Test
	@DisplayName("전체 게시글이 없을 때 빈 리스트 반환 통합 테스트")
	void getAllPostsReturnsEmptyListWhenNoPostsExist() {
		// When
		List<PostReadResponse> response = postService.getAllPosts();

		// Then
		assertThat(response).isEmpty();  // 게시글이 없을 때 빈 리스트 반환
	}

	@Test
	@DisplayName("게시물 수정 성공 통합 테스트")
	void updatePostSuccessIntegrationTest() {
		// Given: 게시글을 먼저 생성
		PostCreateRequest createRequest = new PostCreateRequest("원래 제목", "원래 내용", category.getId(), new String[]{"hashtag1"});
		API<PostCreateResponse> createResponse = postService.createPost(createRequest, member);
		Long postId = createResponse.result().postId();

		// 수정 요청 데이터 준비
		PostUpdateRequest updateRequest = new PostUpdateRequest("수정된 제목", "수정된 내용", category.getId(), new String[]{"hashtag2"});

		// When: 게시글 수정
		API<PostUpdateResponse> updateResponse = postService.updatePost(postId, updateRequest, member);

		// Then: 수정된 결과 검증
		assertThat(updateResponse).isNotNull();
		PostUpdateResponse result = updateResponse.result();
		assertThat(result.postId()).isEqualTo(postId);
		assertThat(result.updatedTitle()).isEqualTo("수정된 제목");
		assertThat(result.updatedContent()).isEqualTo("수정된 내용");

		// DB에서 수정된 게시글을 검증
		Post updatedPost = postRepository.findById(postId).orElse(null);
		assertThat(updatedPost).isNotNull();
		assertThat(updatedPost.getTitle().getTitle()).isEqualTo("수정된 제목");
		assertThat(updatedPost.getContent().getContent()).isEqualTo("수정된 내용");
	}

	@Test
	@DisplayName("존재하지 않는 게시글 수정 시 예외 발생 통합 테스트")
	void updateNonExistentPostThrowsExceptionIntegrationTest() {
		// Given: 존재하지 않는 게시글 ID로 수정 시도
		PostUpdateRequest updateRequest = new PostUpdateRequest("수정된 제목", "수정된 내용", category.getId(), new String[]{"hashtag2"});

		// When & Then: 예외 발생 검증
		assertThatThrownBy(() -> postService.updatePost(999L, updateRequest, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.POST_NOT_FOUND);
	}

	@Test
	@DisplayName("게시물 수정 시 작성자가 아닌 경우 예외 발생 통합 테스트")
	void updatePostByNonAuthorThrowsExceptionIntegrationTest() {
		// Given: 게시글을 먼저 생성
		PostCreateRequest createRequest = new PostCreateRequest("원래 제목", "원래 내용", category.getId(), new String[]{"hashtag1"});
		API<PostCreateResponse> createResponse = postService.createPost(createRequest, member);
		Long postId = createResponse.result().postId();

		// 새로운 작성자 생성 (비밀번호를 유효성 검사를 통과하도록 수정)
		MemberSignUpRequest newMemberRequest = new MemberSignUpRequest("new@mail.com", "newPassword123*", "newNickname", "http://new.url", "010-5678-1234");
		Member newMember = Member.of(newMemberRequest, encryptor);
		memberRepository.save(newMember);

		// 수정 요청 데이터 준비
		PostUpdateRequest updateRequest = new PostUpdateRequest("수정된 제목", "수정된 내용", category.getId(), new String[]{"hashtag2"});

		// When & Then: 예외 발생 검증
		assertThatThrownBy(() -> postService.updatePost(postId, updateRequest, newMember))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.NOT_POST_AUTHOR);
	}

	@Test
	@DisplayName("게시물 삭제 성공 통합 테스트")
	void deletePostSuccessIntegrationTest() {
		// Given: 게시글을 먼저 생성
		PostCreateRequest createRequest = new PostCreateRequest("삭제 테스트 제목", "삭제 테스트 내용", category.getId(), new String[]{"hashtag"});
		API<PostCreateResponse> createResponse = postService.createPost(createRequest, member);
		Long postId = createResponse.result().postId();

		// When: 게시글 삭제
		postService.deletePost(postId, member);

		// Then: 게시글이 데이터베이스에서 삭제되었는지 검증
		Post deletedPost = postRepository.findById(postId).orElse(null);
		assertThat(deletedPost).isNull(); // 삭제되었기 때문에 null이어야 함
	}

	@Test
	@DisplayName("게시글 삭제 시 작성자가 아닌 경우 예외 발생 통합 테스트")
	void deletePostByNonAuthorThrowsExceptionIntegrationTest() {
		// Given: 게시글을 먼저 생성
		PostCreateRequest createRequest = new PostCreateRequest("삭제 테스트 제목", "삭제 테스트 내용", category.getId(), new String[]{"hashtag"});
		API<PostCreateResponse> createResponse = postService.createPost(createRequest, member);
		Long postId = createResponse.result().postId();

		// 새로운 작성자 생성 (비밀번호 유효성 검사를 통과하도록 수정)
		MemberSignUpRequest newMemberRequest = new MemberSignUpRequest("new@mail.com", "newPassword123*", "newNickname", "http://new.url", "010-5678-1234");
		Member newMember = Member.of(newMemberRequest, encryptor);
		memberRepository.save(newMember);

		// When & Then: 작성자가 아닌 사용자가 삭제 시도하면 예외 발생 검증
		assertThatThrownBy(() -> postService.deletePost(postId, newMember))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.NOT_POST_AUTHOR);
	}

	@Test
	@DisplayName("존재하지 않는 게시글 삭제 시 예외 발생 통합 테스트")
	void deleteNonExistentPostThrowsExceptionIntegrationTest() {
		// When & Then: 존재하지 않는 게시글 ID로 삭제 시도 시 예외 발생 검증
		assertThatThrownBy(() -> postService.deletePost(999L, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.POST_NOT_FOUND);
	}

}
