package nl.tudelft.sem.template.voting.domain;

import java.rmi.NoSuchObjectException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.domain.rulevoting.InvalidIdException;
import nl.tudelft.sem.template.voting.domain.rulevoting.InvalidRuleException;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleTooLongException;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


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
     * This is the scheduler to update an association's council.
     * The method checks for the first entry in the election repository.
     * If it exists, it checks if the election's end date is past due.
     * If it is, then the election results are parsed and sent over
     * to the association microservice which then also updates its history.
     * If an OK response status is received then the election entry is deleted.
     */
    @Async
    @Scheduled(fixedRate = 3000, initialDelay = 0)
    public void forwardElectionResults() {
        System.out.println("Executed at : " + new Date());

        // Here we get the first election in the repository by its end date ascendingly.
        Optional<Election> optElection = electionRepository.findFirstByOrderByEndDateAsc();

        if (optElection.isPresent()) {
            Election election = optElection.get();

            if (Instant.now().isAfter(election.getEndDate().toInstant())) {
                String forward = election.getEndDate() + "| ELECTION | " + election.getResults();

                // TODO : replace this with real endpoint
                final String url = "http://localhost:8082/association/update-council-dummy";
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, forward, String.class);

                if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                    electionRepository.delete(election);
                }
            }
        }
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
    public String amendmentRule(VotingType type, Integer associationId, Integer userId, String rule, String amendment)
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
