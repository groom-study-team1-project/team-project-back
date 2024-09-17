package deepdivers.community.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.hashtag.exception.HashtagExceptionType;
import deepdivers.community.domain.hashtag.repository.HashtagRepository;
import deepdivers.community.domain.hashtag.repository.PostHashtagRepository;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.repository.MemberRepository;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.statustype.PostStatusType;
import deepdivers.community.domain.post.exception.CategoryExceptionType;
import deepdivers.community.domain.post.model.PostCategory;
import deepdivers.community.domain.post.repository.CategoryRepository;
import deepdivers.community.domain.post.repository.PostRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@InjectMocks
	private PostService postService;

	@Mock
	private PostRepository postRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private HashtagRepository hashtagRepository;

	@Mock // PostHashtagRepository 모킹 추가
	private PostHashtagRepository postHashtagRepository;

	private Member member;
	private PostCategory category;

	@BeforeEach
	void setUp() {
		// Member 객체 모킹
		member = mock(Member.class); // 필요시 추가 설정 가능
		// PostCategory 객체 초기화
		category = PostCategory.builder().title("Sample Category").build(); // Mock PostCategory
	}

	@Test
	@DisplayName("게시물 생성이 성공적으로 이루어지는 경우를 테스트한다.")
	void createPostSuccessTest() {
		// Given
		PostCreateRequest request = new PostCreateRequest("유효한 제목", "유효한 내용입니다.", 1L, new String[]{"#hashtag"});

		// Mocking category repository to return the category
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		when(hashtagRepository.findByHashtag("#hashtag")).thenReturn(Optional.empty()); // Mocking hashtag repository

		// When
		API<PostCreateResponse> response = postService.createPost(request, member);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.status().code()).isEqualTo(PostStatusType.POST_CREATE_SUCCESS.getCode());
		assertThat(response.result()).isNotNull();
	}

	@Test
	@DisplayName("존재하지 않는 카테고리 ID로 게시물 생성 시 예외가 발생하는지 테스트한다.")
	void createPostWithInvalidCategoryTest() {
		// Given
		PostCreateRequest request = new PostCreateRequest("유효한 제목", "유효한 내용", 999L, new String[]{"#hashtag"});

		// Mocking category repository to return empty
		when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> postService.createPost(request, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", CategoryExceptionType.CATEGORY_NOT_FOUND);
	}

	@Test
	@DisplayName("유효하지 않은 해시태그로 게시물 생성 시 예외가 발생하는지 테스트한다.")
	void createPostWithInvalidHashtagTest() {
		// Given
		PostCreateRequest request = new PostCreateRequest("유효한 제목", "유효한 내용입니다.", 1L, new String[]{"#invalid#hashtag"}); // 내용 길이 수정

		// Mocking category repository to return the category
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

		// When & Then
		assertThatThrownBy(() -> postService.createPost(request, member))
			.isInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("exceptionType", HashtagExceptionType.INVALID_HASHTAG_FORMAT);
	}
}
