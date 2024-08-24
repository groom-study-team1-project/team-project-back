package deepdivers.community.domain.member.repository;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.model.vo.MemberStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsAccountByEmail(String email);
    Boolean existsMemberByNicknameValue(String nickname);

    Optional<Member> findByEmail(String email);
}
