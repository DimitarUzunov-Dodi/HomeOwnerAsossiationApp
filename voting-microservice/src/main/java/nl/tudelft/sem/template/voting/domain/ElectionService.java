package nl.tudelft.sem.template.example.domain;

import org.springframework.stereotype.Service;

@Service
public class ElectionService extends VotingService {

    private final transient ElectionRepository electionRepository;

    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    public boolean applyForCandidate() {
        return false;
    }

    @Override
    public void createVote() {

    }

    @Override
    public boolean verify() {
        return false;
    }

    @Override
    public String castVote() {
        return null;
    }

    @Override
    public String getResults() {
        return null;
    }

    public String createElection(int associationId) {
        Election election = new Election(associationId);
        electionRepository.save(election);
        return "Election was created for association " + associationId + "and date";
    }
}
