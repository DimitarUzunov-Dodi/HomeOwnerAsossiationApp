package nl.tudelft.sem.template.voting.domain;

import java.rmi.NoSuchObjectException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.domain.rulevoting.InvalidIdException;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
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
     * Returns a string representation of the rule vote object
     * corresponding to the provided id together with the current status.
     *
     * @param ruleVotingId              The id of the rule vote object.
     * @return                          A string representation of the rule vote object and the current status.
     * @throws NoSuchObjectException    Thrown when the rule vote object does not exist.
     */
    public String getRuleVoting(Long ruleVotingId) throws NoSuchObjectException, InvalidIdException {
        if (ruleVotingId == null) {
            throw new InvalidIdException("The rule vote id is null.");
        }
        Optional<RuleVoting> optionalRuleVoting = ruleVotingRepository.findById(ruleVotingId);
        String res;
        if (optionalRuleVoting.isPresent()) {
            res = optionalRuleVoting.get().toString();
        } else {
            throw new NoSuchObjectException("There is no open rule vote with the provided ID.");
        }

        Date currentDate = new Date(System.currentTimeMillis());
        Date votingDate = optionalRuleVoting.get().getEndDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(votingDate);
        cal.add(Calendar.DAY_OF_MONTH, 2);

        if (ChronoUnit.SECONDS.between(currentDate.toInstant(), votingDate.toInstant()) > 0) {
            cal.setTime(votingDate);
            return res + System.lineSeparator() + "The voting procedure is still in reviewing."
                    + System.lineSeparator() + "The voting will start on: " + cal.getTime();
        } else if (ChronoUnit.SECONDS.between(currentDate.toInstant(), cal.getTime().toInstant()) > 0) {
            return res + System.lineSeparator() + "You can cast your vote now."
                    + System.lineSeparator() + "The voting will end on: " + cal.getTime();
        } else {
            return res += System.lineSeparator() + "Voting has ended." + System.lineSeparator()
                    + "The results can be accessed through the association.";
        }

    }
}
