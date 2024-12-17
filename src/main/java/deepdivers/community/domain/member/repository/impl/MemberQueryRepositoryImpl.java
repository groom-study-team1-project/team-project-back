package deepdivers.community.domain.member.repository.impl;

import static deepdivers.community.domain.member.model.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.repository.MemberQueryRepository;
import deepdivers.community.infra.aws.s3.S3PresignManager;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final S3PresignManager s3PresignManager;

    @Override
    public MemberProfileResponse getMemberProfile(final Long profileId, final Long viewerId) {
        final MemberProfileResponse memberProfileResponse = queryFactory.select(
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

        Objects.requireNonNull(memberProfileResponse)
            .setImageUrl(s3PresignManager.generateAccessUrl(memberProfileResponse.getImageUrl()));

        return memberProfileResponse;
    }

}
