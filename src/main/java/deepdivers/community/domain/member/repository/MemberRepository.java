package deepdivers.community.domain.member.repository;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.repository.PostQueryRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLowerNickname(String nickname);
    Boolean existsByEmailValue(String email);
    Optional<Member> findByEmailValue(String email);

}
