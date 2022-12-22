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
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GetPendingVotesVotingServiceTest {
    @Autowired
    private transient VotingService votingService;
    @Autowired
    private transient RuleVotingRepository ruleVotingRepository;
    private RuleVoting ruleVoting;

    @Test
    public void typeProposalTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.addVote(Pair.of(1, "for"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findByAssociationId(1).get(0);

        assertThat(votingService.getPendingVotes(1, 1)).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Voting, Vote: for" + System.lineSeparator());
    }

    @Test
    public void typeAmendmentTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", "fesfse", VotingType.AMENDMENT);
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findByAssociationId(1).get(0);

        assertThat(votingService.getPendingVotes(1, 1)).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Amendment, Status: Reviewing" + System.lineSeparator());
    }

    @Test
    public void statusEndedTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.addVote(Pair.of(1, "against"));
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findByAssociationId(1).get(0);

        assertThat(votingService.getPendingVotes(1, 1)).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Ended, Vote: against" + System.lineSeparator());
    }

    @Test
    public void pendingVotesEmptyTest() throws InvalidIdException {
        assertThat(votingService.getPendingVotes(1, 1))
                .isEqualTo("There are no ongoing rule votes corresponding to the association ID: 1.");
    }

    @Test
    public void pendingVotesMultipleTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.addVote(Pair.of(1, "against"));
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);

        this.ruleVoting = new RuleVoting(1, 42, "shaboop", "fessf", VotingType.AMENDMENT);
        this.ruleVoting.addVote(Pair.of(1, "for"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        this.ruleVoting = new RuleVoting(1, 42, "scoop", null, VotingType.PROPOSAL);
        ruleVotingRepository.save(this.ruleVoting);

        List<Long> ruleVoteIds = ruleVotingRepository.findByAssociationId(1).stream()
                .map(Voting::getId).collect(Collectors.toList());

        assertThat(votingService.getPendingVotes(1, 1)).isEqualTo("ID: " + ruleVoteIds.get(0)
                + ", Type: Proposal, Status: Ended, Vote: against" + System.lineSeparator()
                + "ID: " + ruleVoteIds.get(1) + ", Type: Amendment, Status: Voting, Vote: for" + System.lineSeparator()
                + "ID: " + ruleVoteIds.get(2) + ", Type: Proposal, Status: Reviewing" + System.lineSeparator());
    }

    @Test
    public void noVoteTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findByAssociationId(1).get(0);

        assertThat(votingService.getPendingVotes(1, 1)).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Ended, Vote: No vote (abstain)" + System.lineSeparator());
    }

    @Test
    public void abstainVoteTest() throws InvalidIdException {
        this.ruleVoting = new RuleVoting(1, 42, "Bleep", null, VotingType.PROPOSAL);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        this.ruleVoting.addVote(Pair.of(1, "abstain"));
        ruleVotingRepository.save(this.ruleVoting);
        this.ruleVoting = ruleVotingRepository.findByAssociationId(1).get(0);

        assertThat(votingService.getPendingVotes(1, 1)).isEqualTo("ID: " + this.ruleVoting.getId()
                + ", Type: Proposal, Status: Voting, Vote: abstain" + System.lineSeparator());
    }

    @Test
    public void associationIdNullTest() {
        assertThatThrownBy(() -> {
            votingService.getPendingVotes(null, 1);
        }).isInstanceOf(InvalidIdException.class);
    }

}
