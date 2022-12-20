package nl.tudelft.sem.template.voting.domain;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.domain.rulevoting.InvalidIdException;
import nl.tudelft.sem.template.voting.domain.rulevoting.InvalidRuleException;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleTooLongException;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import org.springframework.data.util.Pair;
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
    public String createElection(VotingType type, int associationId, Integer userId, String rule, String amendment) {
        Voting voting = votingFactory.createVoting(type, associationId, userId, rule, amendment);
        return "Voting was created for association " + associationId
                + " and will be held on " + voting.getEndDate().toString() + ".";
    }

    /**
     * Returns the candidates of an active board election in a given association.
     *
     * @return a set of User IDs of candidates.
     */
    public Set<Integer> getCandidates(int associationId) throws IllegalArgumentException {
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
            Election election = optElection.get();

            //Checks if election end date is further away than 2 days
            Date currentDate = new Date(System.currentTimeMillis());
            Date electionEndDate = election.getEndDate();
            Long candidateDeadline = 2L;
            if (ChronoUnit.DAYS.between(currentDate.toInstant(), electionEndDate.toInstant()) < candidateDeadline) {
                throw new IllegalArgumentException("Too late for candidate application.");
            }

            election.addCandidate(userId);
            electionRepository.save(election);
            return "The candidate with ID " + userId + " has been added.";

        } else {
            throw new IllegalArgumentException("Association with ID "
                    + associationId + " does not have an active election.");
        }
    }

    /**
     * Casts a vote for a candidate in the upcoming election, if the date is less than 2 days before the election end.
     *
     * @return a confirmation message.
     */
    public String castVote(int voterId, int associationId, int candidateId) {
        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);
        if (optElection.isPresent()) {
            Election election = optElection.get();

            //Checks if election end date is closer than 2 days
            Date currentDate = new Date(System.currentTimeMillis());
            Date electionEndDate = election.getEndDate();
            Long candidateDeadline = 2L;
            if (ChronoUnit.DAYS.between(currentDate.toInstant(), electionEndDate.toInstant()) >= candidateDeadline) {
                throw new IllegalArgumentException("Too early to cast a vote.");
            }

            //Checks if election has ended
            if (currentDate.compareTo(electionEndDate) > 0) {
                throw new IllegalArgumentException("The election has ended.");
            }

            //Checks if the candidate exists
            if (!election.getCandidateIds().contains(candidateId)) {
                throw new IllegalArgumentException("Candidate with ID "
                        + candidateId + " does not exist.");
            }

            //If the voter already voted, remove previous vote
            for (Pair vote : election.getVotes()) {
                if ((int) vote.getFirst() == voterId) {
                    election.getVotes().remove(vote);
                    break;
                }
            }

            Pair<Integer, Integer> vote = Pair.of(voterId, candidateId);
            election.addVote(vote);
            electionRepository.save(election);
            return "The voter with ID " + voterId + " voted for the candidate with ID " + candidateId + ".";

        } else {
            throw new IllegalArgumentException("Association with ID "
                    + associationId + " does not have an active election.");
        }
    }

    /**
     * Creates a new rule vote for the proposed rule.
     *
     * @param type          The type of voting object to be created by the factory.
     * @param associationId The association for which this rule vote will be taken.
     * @param userId        The user id of the user proposing the rule.
     * @param rule          The rule that is being proposed.
     * @return              A message confirming the creation of the rule vote.
     */
    public String proposeRule(VotingType type, Integer associationId, Integer userId, String rule)
            throws InvalidIdException, InvalidRuleException, RuleTooLongException {
        if (associationId == null) {
            throw new InvalidIdException("The associationID is invalid.");
        } else if (userId == null) {
            throw new InvalidIdException("The userID is invalid.");
        } else if (rule == null) {
            throw new InvalidRuleException("The rule is null.");
        } else if (rule.equals("")) {
            throw new InvalidRuleException("The rule's description is empty.");
        } else if (rule.length() > this.maxRuleLength) {
            throw new RuleTooLongException("The rule description exceeds the maximum length of "
                    + this.maxRuleLength + " characters.");
        }

        Voting voting = votingFactory.createVoting(type, associationId, userId, rule, null);
        return "Rule: \"" + rule + "\" has been proposed by: " + userId + "." +  System.lineSeparator()
                + "The vote will be held on: " + voting.getEndDate();
    }
}
