package nl.tudelft.sem.template.voting.domain;

import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.domain.rulevoting.InvalidIdException;
import nl.tudelft.sem.template.voting.domain.rulevoting.InvalidRuleException;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleTooLongException;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import org.springframework.stereotype.Service;


@Service
public class VotingService {

    private final transient ElectionRepository electionRepository;
    private final transient RuleVotingRepository ruleVotingRepository;
    private final transient VotingFactory votingFactory;
    private final transient int maxRuleLength = 100;


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
     * Creates a new rule vote for the proposed amendment.
     *
     * @param type          The type of voting object to be created by the factory.
     * @param associationId The association for which this rule vote will be taken.
     * @param userId        The user id of the user proposing the rule.
     * @param rule          The rule that is being amended.
     * @param amendment     The amendment for the original rule.
     * @return              A message confirming the creation of the rule vote.
     */
    public String amendmentRule(String type, Integer associationId, Integer userId, String rule, String amendment)
            throws InvalidIdException, InvalidRuleException, RuleTooLongException {
        if (associationId == null) {
            throw new InvalidIdException("The associationID is invalid.");
        } else if (userId == null) {
            throw new InvalidIdException("The userID is invalid.");
        } else if (amendment == null) {
            throw new InvalidRuleException("The amendment is null.");
        } else if (amendment.equals("")) {
            throw new InvalidRuleException("The amendment's description is empty.");
        } else if (rule.equals(amendment)) {
            throw new InvalidRuleException("The amendment does not change the rule.");
        } else if (amendment.length() > this.maxRuleLength) {
            throw new RuleTooLongException("The amendment's description exceeds the maximum length of "
                    + this.maxRuleLength + " characters.");
        }

        Voting voting = votingFactory.createVoting(type, associationId, userId, rule, amendment);
        return "The user: " + userId + " proposes to change the rule: \"" + rule + "\"" + System.lineSeparator()
                + "to: \"" + amendment + "\"" +  System.lineSeparator() + "The vote will be held on: "
                + voting.getEndDate();
    }
}
