package deepdivers.community.domain.post.model.vo;

public enum CategoryStatus {
	ACTIVE,    // 활성 상태
	INACTIVE;  // 비활성화 상태

	public boolean isActive() {
		return this == ACTIVE;
	}
}
