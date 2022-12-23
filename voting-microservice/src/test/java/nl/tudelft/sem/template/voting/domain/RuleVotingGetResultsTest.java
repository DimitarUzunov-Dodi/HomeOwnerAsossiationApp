package nl.tudelft.sem.template.voting.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

public class RuleVotingGetResultsTest {
    RuleVoting ruleVoting;

    /**
     * Set up the tests.
     */
    @BeforeEach
    public void setup() {
        ruleVoting = new RuleVoting(0, "0", "new rule", "none", VotingType.PROPOSAL);
        ruleVoting.addVote(Pair.of("1", "for"));
        ruleVoting.addVote(Pair.of("2", "for"));
        ruleVoting.addVote(Pair.of("3", "against"));
        ruleVoting.addVote(Pair.of("4", "against"));
        ruleVoting.addVote(Pair.of("5", "against"));
        ruleVoting.addVote(Pair.of("6", "abstain"));
    }

    @Test
    public void tallyVotesTest() {
        HashMap<String, Integer> hm = ruleVoting.tallyVotes();

        System.out.println(hm);

        assertThat(hm.get("for")).isEqualTo(2);
        assertThat(hm.get("against")).isEqualTo(3);
        assertThat(hm.get("abstain")).isEqualTo(1);
    }

    @Test
    public void getResultsTest() {
        assertThat(ruleVoting.getResults()).isEqualTo("against=3, abstain=1, for=2");
    }
}
