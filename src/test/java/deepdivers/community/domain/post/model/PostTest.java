package deepdivers.community.domain.post.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import deepdivers.community.domain.hashtag.model.Hashtag;
import deepdivers.community.domain.hashtag.model.PostHashtag;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.exception.PostExceptionType;
import deepdivers.community.domain.post.model.vo.CategoryStatus;
import deepdivers.community.domain.post.model.vo.PostStatus;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.encryptor.Encryptor;
import deepdivers.community.global.utility.encryptor.EncryptorBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PostTest {

	@Autowired
	@EncryptorBean
	private Encryptor encryptor;

	private Member member;
	private PostCategory category;

	@BeforeEach
	void setUp() {
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
				category.getId(),
				List.of("tag1", "tag2")
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
			", 'Test Content', 1, tag1, tag2",
			"'Test Title', , 1, tag1, tag2",
			"'Test Title', 'Test Content', , tag1, tag2",
	})
	@DisplayName("유효하지 않은 게시글 생성 시 예외 발생")
	void createInvalidPost(String title, String content, Long categoryId, String tag1, String tag2) {
		// given
		List<String> hashtags = List.of(tag1, tag2);
		PostSaveRequest request = new PostSaveRequest(title, content, categoryId, hashtags);

		// when, then
		assertThatThrownBy(() -> Post.of(request, category, member))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("게시글에 해시태그 연결 성공")
	void connectHashtagsToPost() {
		// given
		Post post = Post.of(
				new PostSaveRequest("Test Title", "Test Content", category.getId(), List.of("tag1", "tag2")),
				category,
				member
		);
		Set<PostHashtag> hashtags = Set.of(
				new PostHashtag(post, new Hashtag("tag1")),
				new PostHashtag(post, new Hashtag("tag2"))
		);

		// when
		post.connectHashtags(hashtags);

		// then
		assertThat(post.getPostHashtags()).hasSize(2);
		assertThat(post.getHashtags()).containsExactlyInAnyOrder("tag1", "tag2");
	}

	@Test
	@DisplayName("조회수 증가 성공")
	void increaseViewCount() {
		// given
		Post post = Post.of(
				new PostSaveRequest("Test Title", "Test Content", category.getId(), List.of()),
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