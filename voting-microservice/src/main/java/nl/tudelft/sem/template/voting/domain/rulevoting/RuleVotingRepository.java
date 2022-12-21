package nl.tudelft.sem.template.voting.domain.rulevoting;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleVotingRepository extends JpaRepository<RuleVoting, Long> {
    Optional<RuleVoting> findFirstByOrderByEndDateAsc();
}
