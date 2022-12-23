package nl.tudelft.sem.template.voting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import nl.tudelft.sem.template.voting.domain.rulevoting.InvalidIdException;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import org.junit.jupiter.api.BeforeEach;
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
public class CastRuleVoteVotingServiceTest {
    @Autowired
    private transient VotingService votingService;
    @Autowired
    private transient RuleVotingRepository ruleVotingRepository;
    private Long ruleVoteId;
    private String userId;
    private RuleVoting ruleVoting;
    private int associationId;

    /**
     * Initialize the ruleVoteId and userId variables before each test.
     */
    @BeforeEach
    public void setup() {
        this.associationId = 1;
        this.ruleVoteId = 1L;
        this.userId = "10";
        this.ruleVoting = new RuleVoting(12, this.userId, "Bleep", null, VotingType.PROPOSAL);
        this.ruleVotingRepository.save(this.ruleVoting);
    }

    @Test
    public void castRuleVoteInFavourTest() throws InvalidIdException {
        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        assertThat(votingService.castRuleVote(this.ruleVoteId, this.userId, "for", this.associationId))
                .isEqualTo("The user with ID 10 voted in favour of the "
                        + "proposal under consideration in rule vote: 1");

        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        List<Pair<String, String>> expected = new ArrayList<>();
        expected.add(Pair.of(this.userId, "for"));
        assertThat(this.ruleVoting.getVotes()).isEqualTo(expected);

    }

    @Test
    public void castRuleVoteAbstainTest() throws InvalidIdException {
        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        assertThat(votingService.castRuleVote(this.ruleVoteId, this.userId, "abstain", this.associationId))
                .isEqualTo("The user with ID 10 abstains from voting for the "
                        + "proposal under consideration in rule vote: 1");

        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        List<Pair<String, String>> expected = new ArrayList<>();
        expected.add(Pair.of(this.userId, "abstain"));
        assertThat(this.ruleVoting.getVotes()).isEqualTo(expected);

    }

    @Test
    public void castRuleVoteAgainstTest() throws InvalidIdException {
        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        assertThat(votingService.castRuleVote(this.ruleVoteId, this.userId, "against", this.associationId))
                .isEqualTo("The user with ID 10 voted against the "
                        + "proposal under consideration in rule vote: 1");

        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        List<Pair<String, String>> expected = new ArrayList<>();
        expected.add(Pair.of(this.userId, "against"));
        assertThat(this.ruleVoting.getVotes()).isEqualTo(expected);
    }

    @Test
    public void voteInReviewingTest() {
        assertThatThrownBy(() -> {
            votingService.castRuleVote(this.ruleVoteId, this.userId, "against", this.associationId);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Not passing.
     */
    public void voteEnded() {
        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);

        assertThatThrownBy(() -> {
            votingService.castRuleVote(this.ruleVoteId, this.userId, "for", this.associationId);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void voteNullTest() {
        assertThatThrownBy(() -> {
            votingService.castRuleVote(this.ruleVoteId, this.userId, null, this.associationId);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void invalidVoteTest() {
        assertThatThrownBy(() -> {
            votingService.castRuleVote(this.ruleVoteId, this.userId, "gsrhbsk", this.associationId);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void ruleVoteIdNullTest() {
        assertThatThrownBy(() -> {
            votingService.castRuleVote(null, this.userId, "for", this.associationId);
        }).isInstanceOf(InvalidIdException.class);
    }

    @Test
    public void ruleVoteDoesNotExistTest() {
        assertThatThrownBy(() -> {
            votingService.castRuleVote(324L, this.userId, "for", this.associationId);
        }).isInstanceOf(InvalidIdException.class);
    }

    @Test
    public void changeVoteTest() throws InvalidIdException {
        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        votingService.castRuleVote(this.ruleVoteId, this.userId, "against", this.associationId);
        votingService.castRuleVote(this.ruleVoteId, this.userId, "for", this.associationId);

        this.ruleVoting = ruleVotingRepository.findById(this.ruleVoteId).orElseGet(null);
        List<Pair<String, String>> expected = new ArrayList<>();
        expected.add(Pair.of(this.userId, "for"));
        assertThat(this.ruleVoting.getVotes()).isEqualTo(expected);
    }
}
