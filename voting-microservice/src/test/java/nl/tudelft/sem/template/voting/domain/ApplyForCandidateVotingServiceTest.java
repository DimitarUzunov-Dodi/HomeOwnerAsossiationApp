package nl.tudelft.sem.template.voting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Date;
import java.util.Optional;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
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
public class ApplyForCandidateVotingServiceTest {
    @Autowired
    private transient VotingService votingService;
    @Autowired
    private transient ElectionRepository electionRepository;
    private String userId;
    private int associationId;
    long dayInMs = 1000 * 60 * 60 * 24;

    /**
     * Initialize the userId and associationId variables before each test.
     */
    @BeforeEach
    public void setup() {
        userId = "1";
        associationId = 10;
    }

    @Test
    public void applyForCandidateTest() throws IllegalArgumentException {
        Election election = new Election(associationId);
        election.setEndDate(new Date(System.currentTimeMillis() + (int) (2.5 * dayInMs)));
        electionRepository.save(election);

        String result = votingService.applyForCandidate(userId, associationId);
        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);

        assert optElection.isPresent();
        assertThat(optElection.get().getCandidateIds()).containsExactly(userId);
        assertThat(result).isEqualTo("The candidate with ID " + userId + " has been added.");
    }

    @Test
    public void electionTooCloseTest() {
        Election election = new Election(associationId);
        election.setEndDate(new Date(System.currentTimeMillis() + (int) (1.5 * dayInMs)));
        electionRepository.save(election);

        assertThatThrownBy(() -> {
            votingService.applyForCandidate(userId, associationId);
        }).hasMessage("Too late for candidate application.");
    }


    @Test
    public void associationMissingTest() {
        Election election = new Election(associationId + 1);
        election.setEndDate(new Date(System.currentTimeMillis() + (int) (2.5 * dayInMs)));
        electionRepository.save(election);

        assertThatThrownBy(() -> {
            votingService.applyForCandidate(userId, associationId);
        }).hasMessage("Association with ID " + associationId + " does not have an active election.");
    }
}
