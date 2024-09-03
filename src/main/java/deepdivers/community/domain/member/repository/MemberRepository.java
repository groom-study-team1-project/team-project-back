package deepdivers.community.domain.member.repository;

import deepdivers.community.domain.member.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsAccountByEmail(String email);
    Optional<Member> findByNicknameLowerValue(String nickname);

    Optional<Member> findByEmail(String email);
}
