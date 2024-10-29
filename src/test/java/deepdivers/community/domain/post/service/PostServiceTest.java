package deepdivers.community.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import deepdivers.community.domain.post.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.request.PostUpdateRequest;
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
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
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
		MockMultipartFile imageFile = new MockMultipartFile(
				"imageFiles", "test-image.jpg", "image/jpeg", "test-image-content".getBytes()
		);
		API<PostImageUploadResponse> uploadResponse = postService.postImageUpload(imageFile);
		String uploadedImageUrl = uploadResponse.result().imageUrl();

		PostCreateRequest request = new PostCreateRequest("통합 테스트 제목", "통합 테스트 내용", category.getId(), new String[]{"hashtag"}, List.of(uploadedImageUrl));

		API<PostCreateResponse> response = postService.createPost(request, member);

		assertThat(response).isNotNull();
		PostCreateResponse result = response.result();
		assertThat(result.postId()).isNotNull();

		Post savedPost = postRepository.findById(result.postId()).orElse(null);
		assertThat(savedPost).isNotNull();
		assertThat(savedPost.getTitle().getTitle()).isEqualTo("통합 테스트 제목");
		assertThat(savedPost.getContent().getContent()).isEqualTo("통합 테스트 내용");
	}

	@Test
	@DisplayName("존재하지 않는 카테고리로 게시물 생성 시 예외 발생 통합 테스트")
	void createPostWithInvalidCategoryIntegrationTest() {
		PostCreateRequest request = new PostCreateRequest("유효한 제목", "유효한 내용", 999L, new String[]{"hashtag"}, List.of("1"));

		assertThatThrownBy(() -> postService.createPost(request, member))
				.isInstanceOf(BadRequestException.class)
				.hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionType.CATEGORY_NOT_FOUND);
	}

	@Test
	@DisplayName("유효하지 않은 해시태그로 게시물 생성 시 예외 발생 통합 테스트")
	void createPostWithInvalidHashtagIntegrationTest() {
		MockMultipartFile imageFile = new MockMultipartFile(
				"imageFiles", "test-image.jpg", "image/jpeg", "test-image-content".getBytes()
		);
		API<PostImageUploadResponse> uploadResponse = postService.postImageUpload(imageFile);
		String uploadedImageUrl = uploadResponse.result().imageUrl();

		PostCreateRequest request = new PostCreateRequest("유효한 제목", "유효한 내용", category.getId(), new String[]{"invalid#hashtag"}, List.of(uploadedImageUrl));

		assertThatThrownBy(() -> postService.createPost(request, member))
				.isInstanceOf(BadRequestException.class)
				.hasFieldOrPropertyWithValue("exceptionType", HashtagExceptionType.INVALID_HASHTAG_FORMAT);
	}


	@Test
	@DisplayName("게시글 조회 성공 통합 테스트")
	void getPostByIdSuccessIntegrationTest() {
		MockMultipartFile imageFile = new MockMultipartFile(
				"imageFiles", "test-image.jpg", "image/jpeg", "test-image-content".getBytes()
		);
		API<PostImageUploadResponse> uploadResponse = postService.postImageUpload(imageFile);
		String uploadedImageUrl = uploadResponse.result().imageUrl();

		PostCreateRequest createRequest = new PostCreateRequest("조회 테스트 제목", "조회 테스트 내용", category.getId(), new String[]{"hashtag"}, List.of(uploadedImageUrl));
		API<PostCreateResponse> createResponse = postService.createPost(createRequest, member);
		Long postId = createResponse.result().postId();

		PostReadResponse readResponse = postService.getPostById(postId, "127.0.0.1");

		assertThat(readResponse).isNotNull();
		assertThat(readResponse.postId()).isEqualTo(postId);
		assertThat(readResponse.title()).isEqualTo("조회 테스트 제목");
		assertThat(readResponse.content()).isEqualTo("조회 테스트 내용");
		//assertThat(readResponse.hashtags()).containsExactly("hashtag");
	}


	@Test
	@DisplayName("전체 게시글 조회 성공 통합 테스트")
	void getAllPostsSuccessIntegrationTest() {
		MockMultipartFile imageFile = new MockMultipartFile(
				"imageFiles", "test-image.jpg", "image/jpeg", "test-image-content".getBytes()
		);
		API<PostImageUploadResponse> uploadResponse = postService.postImageUpload(imageFile);
		String uploadedImageUrl = uploadResponse.result().imageUrl();

		PostCreateRequest createRequest1 = new PostCreateRequest("게시글 제목 1", "게시글 내용 1", category.getId(), new String[]{"hashtag1", "hashtag2"}, List.of(uploadedImageUrl));
		PostCreateRequest createRequest2 = new PostCreateRequest("게시글 제목 2", "게시글 내용 2", category.getId(), new String[]{"hashtag3", "hashtag4"}, List.of(uploadedImageUrl));
		PostCreateRequest createRequest3 = new PostCreateRequest("게시글 제목 3", "게시글 내용 3", category.getId(), new String[]{"hashtag5", "hashtag6"}, List.of(uploadedImageUrl));

		postService.createPost(createRequest1, member);
		postService.createPost(createRequest2, member);
		postService.createPost(createRequest3, member);

		entityManager.flush();
		entityManager.clear();

		API<PostCountResponse> response = postService.getAllPosts(Long.MAX_VALUE, null);
		List<PostAllReadResponse> postResponses = response.getResult().getPosts();

		assertThat(postResponses).isNotNull();
		assertThat(postResponses.size()).isEqualTo(3);

		assertThat(postResponses.get(0).getTitle()).isEqualTo("게시글 제목 3");
		assertThat(postResponses.get(1).getTitle()).isEqualTo("게시글 제목 2");
		assertThat(postResponses.get(2).getTitle()).isEqualTo("게시글 제목 1");

//		assertThat(postResponses.get(0).getImageUrls()).hasSize(imageFiles3.size());
//		assertThat(postResponses.get(1).getImageUrls()).hasSize(imageFiles2.size());
//		assertThat(postResponses.get(2).getImageUrls()).hasSize(imageFiles1.size());
	}



	@Test
	@DisplayName("게시물 수정 성공 통합 테스트")
	void updatePostSuccessIntegrationTest() {
		MockMultipartFile imageFile = new MockMultipartFile(
				"imageFiles", "test-image.jpg", "image/jpeg", "test-image-content".getBytes()
		);
		API<PostImageUploadResponse> uploadResponse = postService.postImageUpload(imageFile);
		String uploadedImageUrl = uploadResponse.result().imageUrl();

		PostCreateRequest createRequest = new PostCreateRequest("원래 제목", "원래 내용", category.getId(), new String[]{"hashtag1"}, List.of(uploadedImageUrl));
		API<PostCreateResponse> createResponse = postService.createPost(createRequest, member);
		Long postId = createResponse.result().postId();

		PostUpdateRequest updateRequest = new PostUpdateRequest("수정된 제목", "수정된 내용", category.getId(), new String[]{"hashtag2"}, List.of(uploadedImageUrl));

		API<PostUpdateResponse> updateResponse = postService.updatePost(postId, updateRequest, member);

		assertThat(updateResponse).isNotNull();
		PostUpdateResponse result = updateResponse.result();
		assertThat(result.postId()).isEqualTo(postId);
		assertThat(result.updatedTitle()).isEqualTo("수정된 제목");
		assertThat(result.updatedContent()).isEqualTo("수정된 내용");

		Post updatedPost = postRepository.findById(postId).orElse(null);
		assertThat(updatedPost).isNotNull();
		assertThat(updatedPost.getTitle().getTitle()).isEqualTo("수정된 제목");
		assertThat(updatedPost.getContent().getContent()).isEqualTo("수정된 내용");
	}



	@Test
	@DisplayName("존재하지 않는 게시글 수정 시 예외 발생 통합 테스트")
	void updateNonExistentPostThrowsExceptionIntegrationTest() {
		PostUpdateRequest updateRequest = new PostUpdateRequest("수정된 제목", "수정된 내용", category.getId(), new String[]{"hashtag2"}, List.of("http://sad/temp/image.png"));

		assertThatThrownBy(() -> postService.updatePost(999L, updateRequest, member))
				.isInstanceOf(BadRequestException.class)
				.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.POST_NOT_FOUND);
	}

	@Test
	@DisplayName("게시물 수정 시 작성자가 아닌 경우 예외 발생 통합 테스트")
	void updatePostByNonAuthorThrowsExceptionIntegrationTest() {
		MockMultipartFile imageFile = new MockMultipartFile(
				"imageFiles", "test-image.jpg", "image/jpeg", "test-image-content".getBytes()
		);
		API<PostImageUploadResponse> uploadResponse = postService.postImageUpload(imageFile);
		String uploadedImageUrl = uploadResponse.result().imageUrl();
		
		PostCreateRequest createRequest = new PostCreateRequest("원래 제목", "원래 내용", category.getId(), new String[]{"hashtag1"}, List.of(uploadedImageUrl));
		API<PostCreateResponse> createResponse = postService.createPost(createRequest, member);
		Long postId = createResponse.result().postId();

		MemberSignUpRequest newMemberRequest = new MemberSignUpRequest("new@mail.com", "newPassword123*", "newNickname", "http://new.url", "010-5678-1234");
		Member newMember = Member.of(newMemberRequest, encryptor);
		memberRepository.save(newMember);

		PostUpdateRequest updateRequest = new PostUpdateRequest("수정된 제목", "수정된 내용", category.getId(), new String[]{"hashtag2"}, List.of(uploadedImageUrl));

		assertThatThrownBy(() -> postService.updatePost(postId, updateRequest, newMember))
				.isInstanceOf(BadRequestException.class)
				.hasFieldOrPropertyWithValue("exceptionType", PostExceptionType.NOT_POST_AUTHOR);
	}


	@Test
	@DisplayName("게시물 삭제 성공 통합 테스트")
	void deletePostSuccessIntegrationTest() {
		MockMultipartFile imageFile = new MockMultipartFile(
				"imageFiles", "test-image.jpg", "image/jpeg", "test-image-content".getBytes()
		);
		API<PostImageUploadResponse> uploadResponse = postService.postImageUpload(imageFile);
		String uploadedImageUrl = uploadResponse.result().imageUrl();

		PostCreateRequest createRequest = new PostCreateRequest("삭제 테스트 제목", "삭제 테스트 내용", category.getId(), new String[]{"hashtag"}, List.of(uploadedImageUrl));
		API<PostCreateResponse> createResponse = postService.createPost(createRequest, member);
		Long postId = createResponse.result().postId();

		postService.deletePost(postId, member);

		Post deletedPost = postRepository.findById(postId).orElse(null);
		assertThat(deletedPost).isNull();
	}

	@Test
	@DisplayName("게시글 삭제 시 작성자가 아닌 경우 예외 발생 통합 테스트")
	void deletePostByNonAuthorThrowsExceptionIntegrationTest() {
		MockMultipartFile imageFile = new MockMultipartFile(
				"imageFiles", "test-image.jpg", "image/jpeg", "test-image-content".getBytes()
		);
		API<PostImageUploadResponse> uploadResponse = postService.postImageUpload(imageFile);
		String uploadedImageUrl = uploadResponse.result().imageUrl();

		PostCreateRequest createRequest = new PostCreateRequest("삭제 테스트 제목", "삭제 테스트 내용", category.getId(), new String[]{"hashtag"}, List.of(uploadedImageUrl));
		API<PostCreateResponse> createResponse = postService.createPost(createRequest, member);
		Long postId = createResponse.result().postId();

		MemberSignUpRequest newMemberRequest = new MemberSignUpRequest("new@mail.com", "newPassword123*", "newNickname", "http://new.url", "010-5678-1234");
		Member newMember = Member.of(newMemberRequest, encryptor);
		memberRepository.save(newMember);

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
