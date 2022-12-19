package nl.tudelft.sem.template.voting.domain;

import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import org.springframework.stereotype.Service;


@Service
public class VotingService {

    private final transient ElectionRepository electionRepository;
    private final transient RuleVotingRepository ruleVotingRepository;
    private final transient VotingFactory votingFactory;


    /**
     * Instantiates a VotingService object which provides methods to the Voting endpoints,
     * while handling the databases.
     */
    public VotingService(ElectionRepository electionRepository, RuleVotingRepository ruleVotingRepository) {
        this.electionRepository = electionRepository;
        this.ruleVotingRepository = ruleVotingRepository;
        this.votingFactory = new VotingFactory(electionRepository, ruleVotingRepository);
    }

    /**
     * Creates a board election for an association with a given ID.
     *
     * @return a message confirming the creation.
     */
    public String createElection(String type, int associationId, Integer userId, String rule, String amendment) {
        Voting voting = votingFactory.createVoting(type, associationId, userId, rule, amendment);
        return "Voting was created for association " + associationId
                + " and will be held on " + voting.getEndDate().toString() + ".";
    }

    /**
     * Returns the candidates of an active board election in a given association.
     *
     * @return a set of User IDs of candidates.
     */
    public Set<Integer> getCandidates(int associationId) {
        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);
        if (optElection.isPresent()) {
            return optElection.get().getCandidateIds();
        } else {
            throw new IllegalArgumentException("Association with ID "
                    + associationId + " does not have an active election.");
        }
    }

    /**
     * Registers a candidate for the upcoming election, if the date is 2 or more days before the election.
     *
     * @return a confirmation message.
     */
    public String applyForCandidate(int userId, int associationId) {
        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);
        if (optElection.isPresent()) {
            optElection.get().addCandidate(userId);
            return "The candidate with ID " + userId + " has been added.";
        } else {
            throw new IllegalArgumentException("Association with ID "
                    + associationId + " does not have an active election.");
        }
    }
}
