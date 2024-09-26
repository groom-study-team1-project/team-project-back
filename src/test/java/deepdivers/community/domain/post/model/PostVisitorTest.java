package deepdivers.community.domain.post.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

class PostVisitorTest {

	private Post post;
	private PostVisitor postVisitor;

	@BeforeEach
	void setUp() {
		// Mock Post 객체 생성
		post = new Post(); // Post 객체를 실제로 구현해야 합니다.
		post.setId(1L); // ID 설정 (가정)

		// PostVisitor 객체 생성
		postVisitor = new PostVisitor(post, "192.168.0.1");

		// visitedAt를 현재 시간으로 설정
		// 이렇게 초기화하면 canIncreaseViewCount가 true를 반환할 것입니다.
		setVisitedAt(postVisitor, LocalDateTime.now().minusMinutes(31)); // 31분 전으로 설정
	}

	@Test
	@DisplayName("PostVisitor 객체가 성공적으로 생성되는 것을 확인한다.")
	void postVisitorShouldBeCreatedSuccessfully() {
		// then
		assertThat(postVisitor).isNotNull();
		assertThat(postVisitor.getPost()).isEqualTo(post);
		assertThat(postVisitor.getIpAddr()).isEqualTo("192.168.0.1");
		assertThat(postVisitor.getVisitedAt()).isNotNull(); // visitedAt이 null이 아님을 확인
	}

	@Test
	@DisplayName("초기 방문 시 canIncreaseViewCount가 true를 반환하는지 확인한다.")
	void canIncreaseViewCountShouldReturnTrueInitially() {
		// when
		boolean result = postVisitor.canIncreaseViewCount();

		// then
		System.out.println("초기 방문에서 canIncreaseViewCount 결과: " + result);
		assertThat(result).isTrue(); // true 반환을 기대
	}

	@Test
	@DisplayName("방문 시간이 30분 이내일 때 canIncreaseViewCount가 false를 반환하는지 확인한다.")
	void canIncreaseViewCountShouldReturnFalseWithin30Minutes() {
		// visitedAt를 현재 시간으로 설정
		postVisitor.updateVisitedAt();

		// when
		boolean result = postVisitor.canIncreaseViewCount();

		// then
		System.out.println("30분 이내 방문에서 canIncreaseViewCount 결과: " + result);
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("방문 시간이 30분 이상 경과했을 때 canIncreaseViewCount가 true를 반환하는지 확인한다.")
	void canIncreaseViewCountShouldReturnTrueAfter30Minutes() throws NoSuchFieldException, IllegalAccessException {
		// visitedAt를 31분 전으로 설정
		setVisitedAt(postVisitor, LocalDateTime.now().minusMinutes(31));

		// when
		boolean result = postVisitor.canIncreaseViewCount();

		// then
		System.out.println("30분 경과 후 방문에서 canIncreaseViewCount 결과: " + result);
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("updateVisitedAt 메서드가 방문 시간을 업데이트하는지 확인한다.")
	void updateVisitedAtShouldUpdateVisitedAt() {
		LocalDateTime oldVisitedAt = postVisitor.getVisitedAt();

		// visitedAt 업데이트
		postVisitor.updateVisitedAt();

		// visitedAt이 변경되었는지 확인
		assertThat(postVisitor.getVisitedAt()).isAfter(oldVisitedAt);
	}

	// private 메서드를 통해 visitedAt을 설정하는 유틸리티 메서드 추가
	private void setVisitedAt(PostVisitor visitor, LocalDateTime time) {
		try {
			Field visitedAtField = visitor.getClass().getDeclaredField("visitedAt");
			visitedAtField.setAccessible(true); // private 필드에 접근 가능하게 설정
			visitedAtField.set(visitor, time); // 필드 값 설정
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
