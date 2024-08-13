package deepdivers.commuity.domain.member.model.vo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MemberStatus {

    REGISTERED("등록"),
    UNREGISTERED("해지");

    private final String description;

}
