package nl.tudelft.sem.template.voting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
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
public class GetCandidatesVotingServiceTest {
    @Autowired
    private transient VotingService votingService;
    @Autowired
    private transient ElectionRepository electionRepository;
    private int associationId;

    /**
     * Initialize the associationId variable before each test.
     */
    @BeforeEach
    public void setup() {
        associationId = 10;
    }

    @Test
    public void getCandidatesTest() throws IllegalArgumentException {
        Election election = new Election(associationId);
        election.addCandidate(1);
        election.addCandidate(2);
        electionRepository.save(election);

        Set<Integer> result = votingService.getCandidates(associationId);

        assertThat(result).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    public void associationMissingTest() {
        Election election = new Election(associationId + 1);
        electionRepository.save(election);

        assertThatThrownBy(() -> {
            votingService.getCandidates(associationId);
        }).hasMessage("Association with ID " + associationId + " does not have an active election.");
    }
}
