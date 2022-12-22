package nl.tudelft.sem.template.voting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
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
public class CreateElectionVotingServiceTest {
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
    public void createElectionTest() throws IllegalArgumentException {
        String result = votingService.createElection(VotingType.ELECTION, associationId, "a", null, null);

        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);

        assert optElection.isPresent();
        assert optElection.get().getAssociationId() == associationId;
        assertThat(result).contains("Voting was created for association " + associationId + " and will be held on ");
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
