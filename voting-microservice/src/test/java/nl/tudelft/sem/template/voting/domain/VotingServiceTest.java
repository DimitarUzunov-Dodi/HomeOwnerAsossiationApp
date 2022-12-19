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
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class VotingServiceTest {
    @Autowired
    private transient VotingService votingService;
    @Autowired
    private transient RuleVotingRepository ruleVotingRepository;
    private int associationId;
    private int userId;
    private String rule;

    /**
     * Initialize the associationId, userId and rule variables before each test.
     */
    @BeforeEach
    public void setup() {
        this.associationId = 1;
        this.userId = 42;
        this.rule = "One should not murder the other members!";
    }

    @Test
    public void proposeRuleTest() throws RuleTooLongException, InvalidRuleException, InvalidIdException {
        String result = votingService.proposeRule("Proposal", this.associationId, this.userId, this.rule);
        Optional<RuleVoting> voting = ruleVotingRepository.findById(1L);
        Calendar cal = null;
        if (voting.isPresent()) {
            Date date = voting.get().getEndDate();
            cal = Calendar.getInstance();
            cal.setTime(date);
        }

        assert cal != null;
        assertThat(result)
                .isEqualTo("Rule: \"One should not murder the other members!\" has been proposed by: 42."
                        + System.lineSeparator() + "The vote will be held on: " + cal.getTime());
    }

    @Test
    public void nullAssociationIdTest() {
        assertThatThrownBy(() -> {
            votingService.proposeRule("Proposal", null, this.userId, this.rule);
        }).isInstanceOf(InvalidIdException.class);
    }

    @Test
    public void nullUserIdTest() {
        assertThatThrownBy(() -> {
            votingService.proposeRule("Proposal", this.associationId, null, this.rule);
        }).isInstanceOf(InvalidIdException.class);
    }

    @Test
    public void nullRuleTest() {
        assertThatThrownBy(() -> {
            votingService.proposeRule("Proposal", this.associationId, this.userId, null);
        }).isInstanceOf(InvalidRuleException.class);
    }

    @Test
    public void emptyRuleTest() {
        this.rule = "";
        assertThatThrownBy(() -> {
            votingService.proposeRule("Proposal", this.associationId, this.userId, this.rule);
        }).isInstanceOf(InvalidRuleException.class);
    }

    @Test
    public void ruleTooLongTest() {
        this.rule = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        assertThatThrownBy(() -> {
            votingService.proposeRule("Proposal", this.associationId, this.userId, this.rule);
        }).isInstanceOf(RuleTooLongException.class);
    }
}
