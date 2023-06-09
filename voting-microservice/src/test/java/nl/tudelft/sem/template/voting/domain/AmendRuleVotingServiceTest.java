package nl.tudelft.sem.template.voting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
public class AmendRuleVotingServiceTest {
    @Autowired
    private transient VotingService votingService;
    @Autowired
    private transient RuleVotingRepository ruleVotingRepository;
    private int associationId;
    private String userId;
    private String rule;
    private String amendment;
    private VotingType type;

    /**
     * Initialize the associationId, userId, rule and amendment variables before each test.
     */
    @BeforeEach
    public void setup() {
        this.associationId = 1;
        this.userId = "42";
        this.rule = "One should not murder the other members!";
        this.amendment = "One should be allowed to murder the other members!";
        this.type = VotingType.AMENDMENT;
    }

    @Test
    public void proposeAmendmentTest() throws RuleTooLongException, InvalidRuleException, InvalidIdException {
        String result = votingService
                .amendmentRule(this.type, this.associationId, this.userId, this.rule, this.amendment);
        Optional<RuleVoting> voting = ruleVotingRepository.findById(1L);
        Calendar cal = Calendar.getInstance();
        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -2);
        }

        assertThat(result)
                .isEqualTo("The user: 42 proposes to change the rule: \"One should not murder the other members!\""
                        + System.lineSeparator() + "to: \"One should be allowed to murder the other members!\""
                        + System.lineSeparator() + "The vote will be held on: " + cal.getTime());
    }

    @Test
    public void nullAmendmentTest() {
        assertThatThrownBy(() -> {
            votingService.amendmentRule(this.type, this.associationId, this.userId, this.rule, null);
        }).isInstanceOf(InvalidRuleException.class);
    }

    @Test
    public void emptyAmendmentTest() throws RuleTooLongException, InvalidRuleException {
        this.amendment = "";
        String result = votingService
                .amendmentRule(this.type, this.associationId, this.userId, this.rule, this.amendment);
        Optional<RuleVoting> voting = ruleVotingRepository.findById(1L);
        Calendar cal = Calendar.getInstance();
        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -2);
        }

        assertThat(result)
                .isEqualTo("The user: 42 proposes to remove the rule: \"One should not murder the other members!\""
                        + System.lineSeparator() + "The vote will be held on: " + cal.getTime());

    }

    @Test
    public void amendmentTooLongTest() {
        this.amendment = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        assertThatThrownBy(() -> {
            votingService.amendmentRule(this.type, this.associationId, this.userId, this.rule, this.amendment);
        }).isInstanceOf(RuleTooLongException.class);
    }

    @Test
    public void ruleAlreadyInAnotherVote() throws RuleTooLongException, InvalidRuleException {
        votingService.amendmentRule(this.type, this.associationId, this.userId, this.rule, this.amendment);

        assertThatThrownBy(() -> {
            votingService.amendmentRule(this.type, this.associationId, this.userId, this.rule, this.amendment);
        }).isInstanceOf(InvalidRuleException.class);
    }

    @Test
    public void amendmentAlreadyInAnotherVote() throws RuleTooLongException, InvalidRuleException {
        votingService.amendmentRule(this.type, this.associationId, this.userId, this.rule, this.amendment);

        assertThatThrownBy(() -> {
            votingService.amendmentRule(this.type, this.associationId, this.userId, "something", this.amendment);
        }).isInstanceOf(InvalidRuleException.class);
    }
}

