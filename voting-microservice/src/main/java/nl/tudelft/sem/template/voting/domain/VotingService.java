package nl.tudelft.sem.template.voting.domain;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import org.springframework.data.util.Pair;
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
}
