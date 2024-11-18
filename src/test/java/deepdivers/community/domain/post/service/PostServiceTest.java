package deepdivers.community.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import deepdivers.community.global.config.LocalStackTestConfig;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.response.PostAllReadResponse;
import deepdivers.community.domain.post.dto.response.PostCountResponse;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.Post;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.model.vo.CategoryStatus;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.encryptor.Encryptor;
import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@Import(LocalStackTestConfig.class)
class PostServiceTest {

	@Autowired
	private PostService postService;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private EntityManager entityManager; // EntityManager 주입

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
		PostSaveRequest request = new PostSaveRequest("통합 테스트 제목", "통합 테스트 내용", category.getId(), new String[]{"hashtag"});

		// When
		API<PostSaveResponse> response = postService.createPost(request, member);

		// Then
		assertThat(response).isNotNull();
		PostSaveResponse result = response.result();
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
		PostSaveRequest request = new PostSaveRequest("유효한 제목", "유효한 내용", 999L, new String[]{"hashtag"});

		// When & Then
		assertThatThrownBy(() -> postService.createPost(request, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionType.CATEGORY_NOT_FOUND);
	}

	@Test
	@DisplayName("유효하지 않은 해시태그로 게시물 생성 시 예외 발생 통합 테스트")
	void createPostWithInvalidHashtagIntegrationTest() {
		// Given
		PostSaveRequest request = new PostSaveRequest("유효한 제목", "유효한 내용", category.getId(), new String[]{"invalid#hashtag"});

		// When & Then
		assertThatThrownBy(() -> postService.createPost(request, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", HashtagExceptionType.INVALID_HASHTAG_FORMAT);
	}

	@Test
	@DisplayName("게시글 조회 성공 통합 테스트")
	void getPostByIdSuccessIntegrationTest() {
		// Given
		PostSaveRequest createRequest = new PostSaveRequest("조회 테스트 제목", "조회 테스트 내용", category.getId(), new String[]{"hashtag"});
		API<PostSaveResponse> createResponse = postService.createPost(createRequest, member);
		Long postId = createResponse.result().postId(); // 생성된 게시물 ID

		// When
		PostReadResponse readResponse = postService.readPostDetail(postId, "127.0.0.1"); // IP 주소는 임의로 설정

		// Then
		assertThat(readResponse).isNotNull();
		assertThat(readResponse.postId()).isEqualTo(postId);
		assertThat(readResponse.title()).isEqualTo("조회 테스트 제목");
		assertThat(readResponse.content()).isEqualTo("조회 테스트 내용");
	}

	@Test
	@DisplayName("전체 게시글 조회 성공 통합 테스트")
	void getAllPostsSuccessIntegrationTest() {
		// Given: 테스트에 사용할 게시글을 미리 생성
		PostSaveRequest createRequest1 = new PostSaveRequest("게시글 제목 1", "게시글 내용 1", category.getId(), new String[]{"hashtag1", "hashtag2"});
		PostSaveRequest createRequest2 = new PostSaveRequest("게시글 제목 2", "게시글 내용 2", category.getId(), new String[]{"hashtag3", "hashtag4"});
		PostSaveRequest createRequest3 = new PostSaveRequest("게시글 제목 3", "게시글 내용 3", category.getId(), new String[]{"hashtag5", "hashtag6"});

		// 각 게시글을 생성
		postService.createPost(createRequest1, member);
		postService.createPost(createRequest2, member);
		postService.createPost(createRequest3, member);

		// DB에 강제로 반영
		entityManager.flush();  // 트랜잭션을 강제로 DB에 반영
		entityManager.clear();  // 영속성 컨텍스트 초기화

		// When: 전체 게시글 조회 수행 (lastContentId에 큰 값을 전달하여 모든 게시글 조회)
		API<PostCountResponse> response = postService.getAllPosts(Long.MAX_VALUE, null);  // 모든 게시글 조회

		// PostCountResponse에서 posts 리스트 추출
		List<PostAllReadResponse> postResponses = response.getResult().getPosts();

		// Then: 반환된 게시글 리스트 검증
		assertThat(postResponses).isNotNull();
		assertThat(postResponses.size()).isEqualTo(3); // 3개의 게시글이 반환되는지 확인

		// 게시글이 내림차순으로 정렬되었는지 검증
		assertThat(postResponses.get(0).getTitle()).isEqualTo("게시글 제목 3");
		assertThat(postResponses.get(1).getTitle()).isEqualTo("게시글 제목 2");
		assertThat(postResponses.get(2).getTitle()).isEqualTo("게시글 제목 1");
	}


	@Test
	@DisplayName("게시물 수정 성공 통합 테스트")
	void updatePostSuccessIntegrationTest() {
		// Given: 게시글을 먼저 생성
		PostSaveRequest createRequest = new PostSaveRequest("원래 제목", "원래 내용", category.getId(), new String[]{"hashtag1"});
		API<PostSaveResponse> createResponse = postService.createPost(createRequest, member);
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
		PostSaveRequest createRequest = new PostSaveRequest("원래 제목", "원래 내용", category.getId(), new String[]{"hashtag1"});
		API<PostSaveResponse> createResponse = postService.createPost(createRequest, member);
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
		PostSaveRequest createRequest = new PostSaveRequest("삭제 테스트 제목", "삭제 테스트 내용", category.getId(), new String[]{"hashtag"});
		API<PostSaveResponse> createResponse = postService.createPost(createRequest, member);
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
		PostSaveRequest createRequest = new PostSaveRequest("삭제 테스트 제목", "삭제 테스트 내용", category.getId(), new String[]{"hashtag"});
		API<PostSaveResponse> createResponse = postService.createPost(createRequest, member);
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
