package nl.tudelft.sem.template.voting.domain.rulevoting;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.List;
import nl.tudelft.sem.template.voting.domain.VotingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleVotingRepository extends JpaRepository<RuleVoting, Long> {

    Optional<RuleVoting> findFirstByOrderByEndDateAsc();

    @Query("SELECT ruleVoting from RuleVoting ruleVoting where ruleVoting.endDate < :date")
    Optional<Collection<RuleVoting>> findAllFinishedRuleVotings(Date date);

    List<RuleVoting> findAllByAssociationId(int associationId);

    boolean existsByAssociationIdAndRuleAndType(Integer associationId, String rule, VotingType type);

    boolean existsByAssociationIdAndAmendment(Integer associationId, String amendment);
}
