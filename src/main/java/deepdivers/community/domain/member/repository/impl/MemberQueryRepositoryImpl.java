package deepdivers.community.domain.member.repository.impl;

import static deepdivers.community.domain.member.model.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.domain.member.repository.MemberQueryRepository;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.exception.model.BaseException;
import deepdivers.community.infra.aws.s3.S3PresignManager;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final S3PresignManager s3PresignManager;

    @Override
    public MemberProfileResponse getMemberProfile(final Long profileId, final Long viewerId) {
        return Optional.ofNullable(getMemberProfileResponse(profileId, viewerId))
            .map(it -> it.withImageUrl(s3PresignManager.generateAccessUrl(it.getImageUrl())))
            .orElseThrow(() -> new BadRequestException(MemberExceptionType.NOT_FOUND_MEMBER));
    }

    private MemberProfileResponse getMemberProfileResponse(final Long profileId, final Long viewerId) {
        return queryFactory.select(
                Projections.constructor(
                    MemberProfileResponse.class,
                    member.id,
                    member.nickname.value,
                    member.role,
                    member.imageKey,
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
