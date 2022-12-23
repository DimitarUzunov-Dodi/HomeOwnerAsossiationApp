package nl.tudelft.sem.template.voting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.rmi.NoSuchObjectException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import nl.tudelft.sem.template.voting.domain.rulevoting.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GetRuleVotingServiceTest {
    @Autowired
    private transient VotingService votingService;
    @Autowired
    private transient RuleVotingRepository ruleVotingRepository;
    private long ruleVoteId;
    private RuleVoting ruleVoting;

    /**
     * Initialize the rule vote id before each test.
     */
    @BeforeEach
    public void setup() {
        this.ruleVoteId = 1L;
    }

    @Test
    public void getProposalRuleVoteTest() throws NoSuchObjectException, InvalidIdException {
        this.ruleVoting = new RuleVoting(11, "42", "Bleep", null, VotingType.PROPOSAL);
        ruleVotingRepository.save(this.ruleVoting);

        String result = votingService.getRuleVoting(this.ruleVoteId);

        Optional<RuleVoting> voting = ruleVotingRepository.findById(this.ruleVoteId);
        Calendar cal = Calendar.getInstance();
        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -2);
        }

        assertThat(result)
                .isEqualTo("The user: 42 proposes to add the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "The voting procedure is still in reviewing."
                        + System.lineSeparator() + "The voting will start on: " + cal.getTime());
    }

    @Test
    public void proposalCurrentEqualsVotingDateTest() throws NoSuchObjectException, InvalidIdException {
        this.ruleVoting = new RuleVoting(11, "42", "Bleep", null, VotingType.PROPOSAL);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        String result = votingService.getRuleVoting(this.ruleVoteId);

        assertThat(result)
                .isEqualTo("The user: 42 proposes to add the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "You can cast your vote now."
                        + System.lineSeparator() + "The voting will end on: " + cal.getTime());
    }

    @Test
    public void proposalBetweenVotingEndDateTest() throws NoSuchObjectException, InvalidIdException {
        this.ruleVoting = new RuleVoting(11, "42", "Bleep", null, VotingType.PROPOSAL);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        String result = votingService.getRuleVoting(this.ruleVoteId);

        assertThat(result)
                .isEqualTo("The user: 42 proposes to add the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "You can cast your vote now."
                        + System.lineSeparator() + "The voting will end on: " + cal.getTime());
    }

    @Test
    public void proposalCurrentEqualEndDateTest() throws NoSuchObjectException, InvalidIdException {
        this.ruleVoting = new RuleVoting(11, "42", "Bleep", null, VotingType.PROPOSAL);
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);

        String result = votingService.getRuleVoting(this.ruleVoteId);

        assertThat(result)
                .isEqualTo("The user: 42 proposes to add the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "Voting has ended." + System.lineSeparator()
                        + "The results can be accessed through the association.");
    }

    @Test
    public void proposalCurrentAfterEndDateTest() throws NoSuchObjectException, InvalidIdException {
        this.ruleVoting = new RuleVoting(11, "42", "Bleep", null, VotingType.PROPOSAL);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -4);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        String result = votingService.getRuleVoting(this.ruleVoteId);

        assertThat(result)
                .isEqualTo("The user: 42 proposes to add the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "Voting has ended." + System.lineSeparator()
                        + "The results can be accessed through the association.");
    }

    @Test
    public void getAmendmentRuleVoteTest() throws NoSuchObjectException, InvalidIdException {
        this.ruleVoting = new RuleVoting(11, "42", "Bleep", "Bloop", VotingType.AMENDMENT);
        ruleVotingRepository.save(this.ruleVoting);

        String result = votingService.getRuleVoting(this.ruleVoteId);

        Optional<RuleVoting> voting = ruleVotingRepository.findById(this.ruleVoteId);
        Calendar cal = Calendar.getInstance();
        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -2);
        }

        assertThat(result)
                .isEqualTo("The user: 42 proposes to change the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "into:" + System.lineSeparator()
                        + "\"Bloop\"." + System.lineSeparator() + "The voting procedure is still in reviewing."
                        + System.lineSeparator() + "The voting will start on: " + cal.getTime());
    }

    @Test
    public void amendmentCurrentEqualsVotingDateTest() throws NoSuchObjectException, InvalidIdException {
        this.ruleVoting = new RuleVoting(11, "42", "Bleep", "Bloop", VotingType.AMENDMENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        String result = votingService.getRuleVoting(this.ruleVoteId);

        assertThat(result)
                .isEqualTo("The user: 42 proposes to change the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "into:" + System.lineSeparator()
                        + "\"Bloop\"." + System.lineSeparator() + "You can cast your vote now."
                        + System.lineSeparator() + "The voting will end on: " + cal.getTime());
    }

    @Test
    public void amendmentBetweenVotingEndDateTest() throws NoSuchObjectException, InvalidIdException {
        this.ruleVoting = new RuleVoting(11, "42", "Bleep", "Bloop", VotingType.AMENDMENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        String result = votingService.getRuleVoting(this.ruleVoteId);

        assertThat(result)
                .isEqualTo("The user: 42 proposes to change the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "into:" + System.lineSeparator()
                        + "\"Bloop\"." + System.lineSeparator() + "You can cast your vote now."
                        + System.lineSeparator() + "The voting will end on: " + cal.getTime());
    }

    @Test
    public void amendmentCurrentEqualEndDateTest() throws NoSuchObjectException, InvalidIdException {
        this.ruleVoting = new RuleVoting(11, "42", "Bleep", "Bloop", VotingType.AMENDMENT);
        this.ruleVoting.setEndDate(new Date(System.currentTimeMillis()));
        ruleVotingRepository.save(this.ruleVoting);

        String result = votingService.getRuleVoting(this.ruleVoteId);

        assertThat(result)
                .isEqualTo("The user: 42 proposes to change the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "into:" + System.lineSeparator()
                        + "\"Bloop\"." + System.lineSeparator() + "Voting has ended." + System.lineSeparator()
                        + "The results can be accessed through the association.");
    }

    @Test
    public void amendmentCurrentAfterEndDateTest() throws NoSuchObjectException, InvalidIdException {
        this.ruleVoting = new RuleVoting(11, "42", "Bleep", "Bloop", VotingType.AMENDMENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -4);
        this.ruleVoting.setEndDate(cal.getTime());
        ruleVotingRepository.save(this.ruleVoting);

        String result = votingService.getRuleVoting(this.ruleVoteId);

        assertThat(result)
                .isEqualTo("The user: 42 proposes to change the rule:" + System.lineSeparator()
                        + "\"Bleep\"." + System.lineSeparator() + "into:" + System.lineSeparator()
                        + "\"Bloop\"." + System.lineSeparator() + "Voting has ended." + System.lineSeparator()
                        + "The results can be accessed through the association.");
    }

    @Test
    public void nullRuleVoteIdTest() {
        assertThatThrownBy(() -> {
            votingService.getRuleVoting(null);
        }).isInstanceOf(InvalidIdException.class);
    }

    @Test
    public void noMatchingRuleVoteIdTest() {
        assertThatThrownBy(() -> {
            votingService.getRuleVoting(4L);
        }).isInstanceOf(NoSuchObjectException.class);
    }

}
