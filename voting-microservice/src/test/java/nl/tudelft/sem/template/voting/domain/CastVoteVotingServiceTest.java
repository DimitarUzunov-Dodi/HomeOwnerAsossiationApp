package nl.tudelft.sem.template.voting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Date;
import java.util.Optional;
import nl.tudelft.sem.template.voting.domain.election.*;
import org.junit.jupiter.api.BeforeEach;
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
public class CastVoteVotingServiceTest {
    @Autowired
    private transient VotingService votingService;
    @Autowired
    private transient ElectionRepository electionRepository;
    private String voterId;
    private int associationId;
    public String candidateId;
    long dayInMs = 1000 * 60 * 60 * 24;

    /**
     * Initialize the voterId, associationId and candidateId variables before each test.
     */
    @BeforeEach
    public void setup() {
        voterId = "1";
        associationId = 10;
        candidateId = "2";
    }

    @Test
    public void castVoteTest() throws IllegalArgumentException {
        Election election = new Election(associationId);
        election.setEndDate(new Date(System.currentTimeMillis() + (int) (1.5 * dayInMs)));
        election.addCandidate(candidateId);
        electionRepository.save(election);

        String result = votingService.castElectionVote(voterId, associationId, candidateId);
        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);

        assert optElection.isPresent();
        assertThat(optElection.get().getCandidateIds()).containsExactly(candidateId);
        assertThat(result).isEqualTo(
                "The voter with ID " + voterId + " voted for the candidate with ID " + candidateId + ".");
    }

    @Test
    public void electionTooFarAwayTest() {
        Election election = new Election(associationId);
        election.setEndDate(new Date(System.currentTimeMillis() + (int) (2.5 * dayInMs)));
        electionRepository.save(election);

        assertThatThrownBy(() -> {
            votingService.castElectionVote(voterId, associationId, candidateId);
        }).hasMessage("Too early to cast a vote.");
    }

    @Test
    public void electionEnded() {
        Election election = new Election(associationId);
        election.setEndDate(new Date(System.currentTimeMillis() + (int) (-0.5 * dayInMs)));
        electionRepository.save(election);

        assertThatThrownBy(() -> {
            votingService.castElectionVote(voterId, associationId, candidateId);
        }).hasMessage("The election has ended.");
    }

    @Test
    public void candidateMissingTest() {
        Election election = new Election(associationId);
        election.setEndDate(new Date(System.currentTimeMillis() + (int) (0.5 * dayInMs)));
        election.addCandidate(candidateId + 1);
        electionRepository.save(election);

        assertThatThrownBy(() -> {
            votingService.castElectionVote(voterId, associationId, candidateId);
        }).hasMessage("Candidate with ID "
                + candidateId + " does not exist.");
    }

    @Test
    public void changeVoteTest() {
        Election election = new Election(associationId);
        election.setEndDate(new Date(System.currentTimeMillis() + (int) (0.5 * dayInMs)));
        election.addCandidate(candidateId);
        election.addCandidate("abc");
        electionRepository.save(election);

        votingService.castElectionVote(voterId, associationId, candidateId);
        votingService.castElectionVote(voterId, associationId, "abc");

        Election res = electionRepository.findByAssociationId(associationId).get();
        String resCandidate = "";
        int count = 0;
        for (Pair<String, String> vote : res.getVotes()) {
            if (vote.getFirst().equals(voterId)) {
                count++;
                resCandidate = vote.getSecond();
            }
        }

        assert count == 1;
        assert resCandidate.equals("abc");
    }

    @Test
    public void changeVote2Test() {
        Election election = new Election(associationId);
        election.setEndDate(new Date(System.currentTimeMillis() + (int) (0.5 * dayInMs)));
        election.addCandidate(candidateId);
        election.addCandidate("abc");
        electionRepository.save(election);

        votingService.castElectionVote(voterId, associationId, candidateId);
        votingService.castElectionVote("a", associationId, "abc");
        votingService.castElectionVote(voterId, associationId, "abc");

        Election res = electionRepository.findByAssociationId(associationId).get();
        String resCandidate = "";
        int count = 0;
        for (Pair<String, String> vote : res.getVotes()) {
            if (vote.getFirst().equals(voterId)) {
                count++;
                resCandidate = vote.getSecond();
            }
        }

        assert count == 1;
        assert resCandidate.equals("abc");
    }

    @Test
    public void associationMissingTest() {
        Election election = new Election(associationId + 1);
        election.setEndDate(new Date(System.currentTimeMillis() + (int) (2.5 * dayInMs)));
        electionRepository.save(election);

        assertThatThrownBy(() -> {
            votingService.castElectionVote(voterId, associationId, candidateId);
        }).hasMessage("Association with ID " + associationId + " does not have an active election.");
    }
}
