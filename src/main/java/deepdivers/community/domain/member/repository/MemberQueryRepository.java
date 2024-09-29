package deepdivers.community.domain.member.repository;

import deepdivers.community.domain.member.dto.response.MemberProfileResponse;

public interface MemberQueryRepository {

    MemberProfileResponse getMemberProfile(Long profileId, Long viewerId);

}
