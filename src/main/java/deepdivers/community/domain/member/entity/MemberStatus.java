package deepdivers.community.domain.member.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MemberStatus {

    REGISTERED("등록"),
    UNREGISTERED("해지"),
    DORMANCY("휴면");

    private final String description;

}
