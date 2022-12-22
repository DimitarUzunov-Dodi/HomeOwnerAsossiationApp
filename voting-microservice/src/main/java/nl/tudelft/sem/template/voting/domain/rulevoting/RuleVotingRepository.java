package nl.tudelft.sem.template.voting.domain.rulevoting;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleVotingRepository extends JpaRepository<RuleVoting, Long> {
    Optional<RuleVoting> findFirstByOrderByEndDateAsc();

    @Query("SELECT ruleVoting from RuleVoting ruleVoting where ruleVoting.endDate < :date")
    Optional<Collection<RuleVoting>> findAllFinishedRuleVotings(Date date);
}
