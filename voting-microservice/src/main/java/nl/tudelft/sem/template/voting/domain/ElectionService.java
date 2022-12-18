package nl.tudelft.sem.template.voting.domain;

import java.util.Set;
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


    /**
     * Creates a board election for an association with a given ID.
     *
     * @return a message confirming the creation.
     */
    public String createElection(int associationId) {
        Election election = new Election(associationId);
        electionRepository.save(election);
        return "Election was created for association " + associationId
                + " and will be held on " + election.getEndDate().toString() + ".";
    }

    /**
     * Returns the candidates of an active board election in a given association.
     *
     * @return a set of User IDs of candidates.
     */
    public Set<Integer> getCandidates(int associationId) {
        var optElection = electionRepository.findByAssociationId(associationId);
        if (optElection.isPresent()) {
            return optElection.get().getCandidates();
        } else {
            throw new IllegalArgumentException("Association with ID "
                    + associationId + " does not have an active election.");
        }
    }
}
