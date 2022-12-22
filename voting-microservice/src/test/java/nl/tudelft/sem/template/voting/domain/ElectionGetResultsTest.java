package nl.tudelft.sem.template.voting.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import nl.tudelft.sem.template.voting.domain.election.Election;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

public class ElectionGetResultsTest {

    Election election;

    /**
     * Set up the tests.
     */
    @BeforeEach
    public void setup() {
        election = new Election(0);
        election.addVote(Pair.of("1", "2"));
        election.addVote(Pair.of("2", "3"));
        election.addVote(Pair.of("3", "1"));
        election.addVote(Pair.of("4", "2"));
        election.addVote(Pair.of("5", "2"));

        for (int i = 1; i <= 5; i++) {
            election.addCandidate(String.valueOf(i));
        }
    }

    @Test
    public void tallyVotesTest() {
        HashMap<String, Integer> hm = election.tallyVotes();

        assertThat(hm.get("2")).isEqualTo(3);
        assertThat(hm.get("3")).isEqualTo(1);
    }

    @Test
    public void getResultsTest() {
        assertThat(election.getResults()).isEqualTo("1=1, 2=3, 3=1, 4=0, 5=0");
    }
}
