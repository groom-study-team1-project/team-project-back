package deepdivers.community.domain.post.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import deepdivers.community.domain.category.entity.CategoryType;
import deepdivers.community.domain.category.entity.PostCategory;
import deepdivers.community.domain.common.PostRequestFactory;
import deepdivers.community.domain.common.exception.NotFoundException;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.domain.PostCreator;
import deepdivers.community.domain.post.domain.adaptor.GeneralPostAdaptor;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.exception.PostExceptionCode;
import deepdivers.community.global.utility.encryptor.PasswordEncryptor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostTest {

	private Member member;
	private PostCategory postCategory;

	@BeforeEach
	void setUp() {
		PasswordEncryptor passwordEncryptor = mock(PasswordEncryptor.class);
		when(passwordEncryptor.encrypt(anyString())).thenReturn("encryptedPassword");
		member = Member.of(
			new MemberSignUpRequest("test@email.com", "test1234!", "test", "test", "010-1234-5678"),
			passwordEncryptor
		);
		postCategory = new PostCategory("", "", CategoryType.GENERAL);
	}

	@Test
	@DisplayName("유효한 게시글 생성")
	void createValidPost() {
		// given
		PostSaveRequest request = PostRequestFactory.createPostSaveRequest();
		PostCreator postCreator = new GeneralPostAdaptor(request, postCategory, member);

		// when
		Post post = Post.of(postCreator);

		// then
		assertThat(post.getTitle().getTitle()).isEqualTo("Post Title");
		assertThat(post.getContent().getContent()).isEqualTo("Post Content");
		assertThat(post.getCategory()).isEqualTo(postCategory);
		assertThat(post.getMember()).isEqualTo(member);
		assertThat(post.getStatus()).isEqualTo(PostStatus.ACTIVE);
		assertThat(post.getCommentCount()).isZero();
		assertThat(post.getViewCount()).isZero();
		assertThat(post.getLikeCount()).isZero();
	}

	@Test
	void 게시글을_생성한_member의_게시글_수가_증가한다() {
		// given
		PostSaveRequest request = PostRequestFactory.createPostSaveRequest();
		PostCreator postCreator = new GeneralPostAdaptor(request, postCategory, member);
		Integer postCount = member.getActivityStats().getPostCount();

		// when
		Post post = Post.of(postCreator);

		// then
		assertThat(post.getMember().getActivityStats().getPostCount()).isEqualTo(postCount + 1);
	}

	@Test
	void 게시글_업데이트가_된다() {
		// given
		PostSaveRequest request = PostRequestFactory.createPostSaveRequest();
		PostCreator postCreator = new GeneralPostAdaptor(request, postCategory, member);
		Post post = Post.of(postCreator);

		PostSaveRequest updateReq = new PostSaveRequest("newTitle", "newContent", "newUrl", 2L, List.of(), List.of(""));
		PostCategory newPostCategory = new PostCategory("new", "new", CategoryType.GENERAL);
		PostCreator postUpdateCreator = new GeneralPostAdaptor(updateReq, newPostCategory, member);

		// when
		post = post.update(postUpdateCreator);

		// then
		assertThat(post.getTitle().getTitle()).isEqualTo("newTitle");
		assertThat(post.getContent().getContent()).isEqualTo("newContent");
		assertThat(post.getCategory()).isEqualTo(newPostCategory);
		assertThat(post.getMember()).isEqualTo(member);
	}

	@Test
	void 게시글_상태가_제거된_상태가_된다() {
		// given
		PostSaveRequest request = PostRequestFactory.createPostSaveRequest();
		PostCreator postCreator = new GeneralPostAdaptor(request, postCategory, member);
		Post post = Post.of(postCreator);

		// when
		post.deletePost();

		// then
		assertThat(post.getStatus()).isEqualTo(PostStatus.DELETED);
	}

	@Test
	void 게시글_상태가_제거된_상태에서_제거를_하면_예외가_발생한다() {
		// given
		PostSaveRequest request = PostRequestFactory.createPostSaveRequest();
		PostCreator postCreator = new GeneralPostAdaptor(request, postCategory, member);
		Post post = Post.of(postCreator);
		post.deletePost();

		// when & then
		assertThatThrownBy(post::deletePost)
			.isInstanceOf(NotFoundException.class)
			.hasFieldOrPropertyWithValue("exceptionType", PostExceptionCode.POST_NOT_FOUND);
	}

}