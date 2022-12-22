package nl.tudelft.sem.template.voting.domain;

import java.rmi.NoSuchObjectException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.domain.models.ElectionResultRequestModel;
import nl.tudelft.sem.template.voting.domain.models.RuleVoteResultRequestModel;
import nl.tudelft.sem.template.voting.domain.rulevoting.InvalidIdException;
import nl.tudelft.sem.template.voting.domain.rulevoting.InvalidRuleException;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleTooLongException;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@EnableScheduling
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
    @Scheduled(fixedRate = 2000, initialDelay = 0)
    public void forwardElectionResults() {
        System.out.println("Executed at : " + new Date());
        Optional<Election> optElection = electionRepository.findFirstByOrderByEndDateAsc();

        if (optElection.isPresent()) {
            Election election = optElection.get();

            if (Instant.now().isAfter(election.getEndDate().toInstant())) {
                // TODO : replace this with real endpoint
                final String url = "http://localhost:8084/association/update-council-dummy";

                ElectionResultRequestModel model = new ElectionResultRequestModel();
                model.setDate(new Date());
                model.setResult(election.getResults());

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer "
                        + SecurityContextHolder.getContext().getAuthentication().getCredentials());
                HttpEntity<ElectionResultRequestModel> request = new HttpEntity<>(model, headers);

                RestTemplate restTemplate = new RestTemplate();

                try {
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);

                    if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                        electionRepository.delete(election);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    /**
     * This is the scheduler to update an association's rule set.
     * The method checks for the first entry in the rulevote repository.
     * If it exists, it checks if the rulevote's end date is past due.
     * If it is, then the rulevote results are parsed and sent over
     * to the association microservice which then also updates its history.
     * If an OK response status is received then the rulevote entry is deleted.
     */
    @Async
    @Scheduled(fixedRate = 2000, initialDelay = 0)
    public void forwardRuleVoteResults() {
        Optional<RuleVoting> optRuleVoting = ruleVotingRepository.findFirstByOrderByEndDateAsc();

        if (optRuleVoting.isPresent()) {
            RuleVoting ruleVoting = optRuleVoting.get();

            if (Instant.now().isAfter(ruleVoting.getEndDate().toInstant())) {
                // TODO : replace this with real endpoint
                final String url = "http://localhost:8084/association/update-rules-dummy";

                RuleVoteResultRequestModel model = new RuleVoteResultRequestModel();
                model.setDate(new Date());
                model.setType(ruleVoting.getType().toString());
                model.setPassed(ruleVoting.passedMotion());
                model.setResult(ruleVoting.getResults());

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer "
                        + SecurityContextHolder.getContext().getAuthentication().getCredentials());
                HttpEntity<RuleVoteResultRequestModel> request = new HttpEntity<>(model, headers);

                RestTemplate restTemplate = new RestTemplate();

                try {
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);

                    if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                        ruleVotingRepository.delete(ruleVoting);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
    public String castElectionVote(int voterId, int associationId, int candidateId) {
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

    /**
     * Returns a message indicating what the user voted.
     *
     * @param ruleVoteId            The id of the rule voting object to vote for.
     * @param userId                The id of the user voting.
     * @param vote                  The vote of the user voting.
     * @return                      A message confirming what the user voted.
     * @throws InvalidIdException   Thrown when the rule vote id is invalid.
     */
    public String castRuleVote(Long ruleVoteId, int userId, String vote) throws InvalidIdException {
        List<String> validVotes = List.of("for", "against", "abstain");
        if (vote == null || !(validVotes.contains(vote))) {
            throw new IllegalArgumentException("The vote is not valid, please pick from: for/against/abstain.");
        } else if (ruleVoteId == null) {
            throw new InvalidIdException("The rule vote id is null.");
        }

        Optional<RuleVoting> optionalRuleVoting = ruleVotingRepository.findById(ruleVoteId);
        RuleVoting ruleVoting = optionalRuleVoting
                .orElseThrow(() -> new InvalidIdException("There is no rule vote ongoing with the id: " + ruleVoteId));

        Date currentDate = new Date(System.currentTimeMillis());
        Date ruleVoteEndDate = ruleVoting.getEndDate();

        if (ChronoUnit.DAYS.between(currentDate.toInstant(), ruleVoteEndDate.toInstant()) > 0) {
            throw new IllegalArgumentException("The rule vote is still in reviewing. It is too early to cast a vote.");
        }

        //Checks if rule vote has ended
        Calendar cal = Calendar.getInstance();
        cal.setTime(ruleVoteEndDate);
        cal.add(Calendar.DAY_OF_MONTH, 2);
        if (currentDate.compareTo(cal.getTime()) > 0) {
            throw new IllegalArgumentException("The rule vote has ended.");
        }

        //If the voter already voted, remove previous vote
        for (Pair v : ruleVoting.getVotes()) {
            if ((int) v.getFirst() == userId) {
                ruleVoting.getVotes().remove(v);
                break;
            }
        }

        Pair<Integer, String> submission = Pair.of(userId, vote);
        ruleVoting.addVote(submission);
        ruleVotingRepository.save(ruleVoting);

        if (vote.equals("for")) {
            return "The user with ID " + userId + " voted in favour of the "
                    + "proposal under consideration in rule vote: " + ruleVoteId;
        } else if (vote.equals("against")) {
            return "The user with ID " + userId + " voted against the "
                    + "proposal under consideration in rule vote: " + ruleVoteId;
        } else {
            return "The user with ID " + userId + " abstains from voting for the "
                    + "proposal under consideration in rule vote: " + ruleVoteId;
        }
    }
}
