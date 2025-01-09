package deepdivers.community.domain.member.repository;

import static deepdivers.community.domain.member.entity.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.exception.MemberExceptionCode;
import deepdivers.community.domain.member.controller.interfaces.MemberQueryRepository;
import deepdivers.community.domain.common.exception.NotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public MemberProfileResponse getMemberProfile(final Long profileId, final Long viewerId) {
        return Optional.ofNullable(getMemberProfileResponse(profileId, viewerId))
            .orElseThrow(() -> new NotFoundException(MemberExceptionCode.NOT_FOUND_MEMBER));
    }

    private MemberProfileResponse getMemberProfileResponse(final Long profileId, final Long viewerId) {
        return queryFactory.select(
                Projections.constructor(
                    MemberProfileResponse.class,
                    member.id,
                    member.nickname.value,
                    member.role,
                    member.image.imageUrl,
                    member.aboutMe,
                    member.phoneNumber.value,
                    member.job,
                    member.githubAddr,
                    member.blogAddr,
                    member.activityStats.postCount,
                    member.activityStats.commentCount,
                    member.id.eq(viewerId)))
            .from(member)
            .where(member.id.eq(profileId))
            .fetchOne();
    }

}
