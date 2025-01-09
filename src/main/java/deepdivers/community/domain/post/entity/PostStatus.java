package deepdivers.community.domain.post.entity;

public enum PostStatus {
    ACTIVE,    // 활성 상태
    DELETED,   // 삭제 상태
    INACTIVE;  // 비활성화 상태

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}
