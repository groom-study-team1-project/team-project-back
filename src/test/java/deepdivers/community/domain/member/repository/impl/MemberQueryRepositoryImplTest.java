package deepdivers.community.domain.member.repository.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import deepdivers.community.domain.RepositoryTest;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.exception.MemberExceptionType;
import deepdivers.community.global.exception.model.NotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberQueryRepositoryImplTest extends RepositoryTest {

    @Test
    void 프로필_조회를_할_수_있다() {
        // given
        Long memberId = 1L;

        // when
        MemberProfileResponse result = memberQueryRepository.getMemberProfile(memberId, 0L);

        // then
        assertThat(result.getId()).isEqualTo(memberId);
    }

    @Test
    void 조회자_본인의_프로필을_조회한_경우_myProfile이_true이다() {
        // given
        Long memberId = 1L;
        Long viewerId = 1L;

        // when
        MemberProfileResponse result = memberQueryRepository.getMemberProfile(memberId, viewerId);

        // then
        assertThat(result.isMyProfile()).isTrue();
    }

    @Test
    void 조회자_본인의_프로필이_아닌_경우_myProfile이_false이다() {
        // given
        Long memberId = 1L;
        Long viewerId = 0L;

        // when
        MemberProfileResponse result = memberQueryRepository.getMemberProfile(memberId, viewerId);

        // then
        assertThat(result.isMyProfile()).isFalse();
    }

    @Test
    void 존재하지_않는_프로필_조회시_예외가_발생한다() {
        // given
        Long memberId = 11L;
        Long viewerId = 0L;

        // when & then
        assertThatThrownBy(() -> memberQueryRepository.getMemberProfile(memberId, viewerId))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("exceptionType", MemberExceptionType.NOT_FOUND_MEMBER);
    }

}