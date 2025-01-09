package deepdivers.community.domain.post.entity;

public enum CategoryStatus {
	ACTIVE,    // 활성 상태
	INACTIVE;  // 비활성화 상태

	public boolean isActive() {
		return this == ACTIVE;
	}
}
