package deepdivers.community.domain.member.repository;

import deepdivers.community.domain.member.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLowerNickname(String nickname);

    Boolean existsByEmailValue(String email);

    Optional<Member> findByEmailValue(String email);

}
