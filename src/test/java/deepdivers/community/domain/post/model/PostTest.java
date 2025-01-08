package deepdivers.community.domain.post.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.vo.CategoryStatus;
import deepdivers.community.domain.post.model.vo.PostStatus;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.encryptor.Encryptor;
import deepdivers.community.global.utility.encryptor.EncryptorBean;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

class PostTest {

	@Autowired
	@EncryptorBean
	private Encryptor encryptor;

	private Member member;
	private PostCategory category;

	@BeforeEach
	void setUp() {
		encryptor = mock(Encryptor.class);

		when(encryptor.encrypt(anyString())).thenReturn("encryptedPassword");

		MemberSignUpRequest signUpRequest = new MemberSignUpRequest(
				"test@mail.com",
				"password123*",
				"nickname",
				"http://image.url",
				"010-1234-5678"
		);
		member = Member.of(signUpRequest, encryptor);

		category = PostCategory.createCategory("Category", "Description", CategoryStatus.ACTIVE);
	}

	@Test
	@DisplayName("유효한 게시글 생성")
	void createValidPost() {
		// given
		PostSaveRequest request = new PostSaveRequest(
				"Test Title",
				"Test Content",
				"",
				category.getId(),
				List.of("tag1", "tag2"),
				List.of("http/temp/f.jpeg")
		);

		// when
		Post post = Post.of(request, category, member);

		// then
		assertThat(post).isNotNull();
		assertThat(post.getTitle().getTitle()).isEqualTo("Test Title");
		assertThat(post.getContent().getContent()).isEqualTo("Test Content");
		assertThat(post.getCategory()).isEqualTo(category);
		assertThat(post.getMember()).isEqualTo(member);
		assertThat(post.getStatus()).isEqualTo(PostStatus.ACTIVE);
	}

	@ParameterizedTest
	@CsvSource({
			", 'Test Content', http/temp/f.jpeg,1, tag1, tag2, http/temp/f.jpeg",
			"'Test Title', , http/temp/f.jpeg,  1, tag1, tag2, http/temp/f.jpeg",
	})
	@DisplayName("유효하지 않은 게시글 생성 시 예외 발생")
	void createInvalidPost(String title, String content, String thumbnail, Long categoryId, String tag1, String tag2, String imageUrl) {
		// given
		List<String> hashtags = List.of(tag1, tag2);
		List<String> imageUrls = List.of(imageUrl);
		PostSaveRequest request = new PostSaveRequest(title, content, thumbnail, categoryId, hashtags, imageUrls);

		// when, then
		assertThatThrownBy(() -> Post.of(request, category, member))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining(PostExceptionType.VALUE_CANNOT_BE_NULL.getMessage());
	}

	@Test
	@DisplayName("조회수 증가 성공")
	void increaseViewCount() {
		// given
		Post post = Post.of(
				new PostSaveRequest("Test Title", "Test Content", "", category.getId(), List.of(), List.of("http/temp/f.jpeg")),
				category,
				member
		);

		// when
		post.increaseViewCount();
		post.increaseViewCount();

		// then
		assertThat(post.getViewCount()).isEqualTo(2);
	}

}