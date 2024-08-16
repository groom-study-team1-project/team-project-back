package deepdivers.community.domain.member.repository;

import deepdivers.community.domain.member.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
