package nl.tudelft.sem.template.voting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.*;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.voting.domain.rulevoting.InvalidIdException;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GetPendingVotesVotingServiceTest {
    @Autowired
    private transient VotingService votingService;
    @Autowired
    private transient RuleVotingRepository ruleVotingRepository;
    private RuleVoting ruleVoting;

    @Test
    public void typeProposalTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, "Jeff", "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.addVote(Pair.of("Gerard", "for"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findAllByAssociationId(1).get(0);

        assertThat(votingService.getPendingVotes(1, "Gerard")).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Voting, Your vote: for" + System.lineSeparator());
    }

    @Test
    public void typeAmendmentTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, "Jeff", "Bleep", "fesfse", VotingType.AMENDMENT);
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findAllByAssociationId(1).get(0);

        assertThat(votingService.getPendingVotes(1, "Gerard")).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Amendment, Status: Reviewing" + System.lineSeparator());
    }

    @Test
    public void statusEndedTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, "Jeff", "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.addVote(Pair.of("Gerard", "against"));
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findAllByAssociationId(1).get(0);

        assertThat(votingService.getPendingVotes(1, "Gerard")).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Ended, Your vote: against" + System.lineSeparator());
    }

    @Test
    public void pendingVotesEmptyTest() throws InvalidIdException {
        assertThat(votingService.getPendingVotes(1, "Gerard"))
                .isEqualTo("There are no ongoing rule votes corresponding to the association ID: 1.");
    }

    @Test
    public void pendingVotesMultipleTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, "Jeff", "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.addVote(Pair.of("Gerard", "against"));
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);

        this.ruleVoting = new RuleVoting(1, "Jeff", "shaboop", "fessf", VotingType.AMENDMENT);
        this.ruleVoting.addVote(Pair.of("Gerard", "for"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        this.ruleVoting = new RuleVoting(1, "Jeff", "scoop", null, VotingType.PROPOSAL);
        ruleVotingRepository.save(this.ruleVoting);

        List<Long> ruleVoteIds = ruleVotingRepository.findAllByAssociationId(1).stream()
                .map(Voting::getId).collect(Collectors.toList());

        assertThat(votingService.getPendingVotes(1, "Gerard")).isEqualTo("ID: " + ruleVoteIds.get(0)
                + ", Type: Proposal, Status: Ended, Your vote: against" + System.lineSeparator()
                + "ID: " + ruleVoteIds.get(1) + ", Type: Amendment, Status: Voting, Your vote: for" + System.lineSeparator()
                + "ID: " + ruleVoteIds.get(2) + ", Type: Proposal, Status: Reviewing" + System.lineSeparator());
    }

    @Test
    public void noVoteTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, "Jeff", "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findAllByAssociationId(1).get(0);

        assertThat(votingService.getPendingVotes(1, "Gerard")).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Ended, Your vote: No vote (abstain)" + System.lineSeparator());
    }

    @Test
    public void abstainVoteTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, "Jeff", "Bleep", null, VotingType.PROPOSAL);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        this.ruleVoting.addVote(Pair.of("Gerard", "abstain"));
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findAllByAssociationId(1).get(0);

        assertThat(votingService.getPendingVotes(1, "Gerard")).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Voting, Your vote: abstain" + System.lineSeparator());
    }

    @Test
    public void associationIdNullTest() {
        assertThatThrownBy(() -> {
            votingService.getPendingVotes(null, "Gerard");
        }).isInstanceOf(InvalidIdException.class);
    }

}
