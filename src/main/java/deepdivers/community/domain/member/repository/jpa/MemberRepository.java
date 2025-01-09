package deepdivers.community.domain.member.repository.jpa;

import deepdivers.community.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByNicknameLower(String nickname);
    Boolean existsByEmailValue(String email);
    Optional<Member> findByEmailValue(String email);

}
