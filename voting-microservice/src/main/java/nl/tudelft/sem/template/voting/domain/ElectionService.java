package nl.tudelft.sem.template.voting.domain;

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


    /**
     * Creates a board election for an association with a given ID.
     *
     * @return a message confirming the creation.
     */
    public String createElection(int associationId) {
        Election election = new Election(associationId);
        electionRepository.save(election);
        System.out.println("DONE");
        return "Election was created for association " + associationId
                + " and will be held on " + election.getEndDate().toString() + ".";
    }
}
