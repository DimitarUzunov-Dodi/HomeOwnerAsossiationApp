package nl.tudelft.sem.template.voting.domain;

import java.rmi.NoSuchObjectException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.domain.rulevoting.*;
import nl.tudelft.sem.template.voting.models.AssociationProposalRequestModel;
import nl.tudelft.sem.template.voting.models.ElectionResultRequestModel;
import nl.tudelft.sem.template.voting.models.RuleVoteResultRequestModel;
import nl.tudelft.sem.template.voting.models.UserAssociationRequestModel;
import nl.tudelft.sem.template.voting.utils.RequestUtil;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * "@EnableScheduling" enables the methods annotated with "@Scheduled".
 * They automatically send election and rule voting results to
 * the Association and then delete them from their respective repos.
 * The annotation should be disabled if one opts for manual updates
 * or if scheduling is implemented on the association's side.
 */
@EnableScheduling
@Service
public class VotingService {

    private final transient ElectionRepository electionRepository;
    private final transient RuleVotingRepository ruleVotingRepository;
    private final transient VotingFactory votingFactory;
    private final transient RequestUtil requestUtil;
    private final transient int maxRuleLength = 100;
    private final transient String username = "VotingService";
    private final transient String password = "SuperSecretPassword";
    private final transient String auth = "Authorization";
    private final transient String bearer = "Bearer";


    /**
     * Instantiates a VotingService object which provides methods to the Voting endpoints,
     * while handling the databases.
     */
    public VotingService(ElectionRepository electionRepository, RuleVotingRepository ruleVotingRepository,
                         RequestUtil requestUtil) {
        this.electionRepository = electionRepository;
        this.ruleVotingRepository = ruleVotingRepository;
        this.votingFactory = new VotingFactory(electionRepository, ruleVotingRepository);
        this.requestUtil = requestUtil;
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
    public void forwardElectionResultsScheduler() {
        System.out.println("Executed at : " + new Date());
        Optional<Election> optElection = electionRepository.findFirstByOrderByEndDateAsc();

        if (optElection.isPresent()) {
            Election election = optElection.get();

            if (Instant.now().isAfter(election.getEndDate().toInstant())) {
                final String url = "http://localhost:8084/association/update-council";

                ElectionResultRequestModel model = new ElectionResultRequestModel();
                model.setDate(election.getEndDate());
                model.setAssociationId(election.getAssociationId());
                model.setStandings(election.tallyVotes());
                model.setResult(election.getResults());

                String token = requestUtil.authenticateService(username,
                        password);

                HttpHeaders headers = new HttpHeaders();
                headers.set(auth, bearer
                        + token);
                HttpEntity<ElectionResultRequestModel> request = new HttpEntity<>(model, headers);

                RestTemplate restTemplate = new RestTemplate();

                try {
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);

                    if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                        electionRepository.delete(election);
                        createElection(VotingType.ELECTION, election.getAssociationId(), null, null,
                                null);
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
    public void forwardRuleVoteResultsScheduler() {
        Optional<RuleVoting> optRuleVoting = ruleVotingRepository.findFirstByOrderByEndDateAsc();

        if (optRuleVoting.isPresent()) {
            RuleVoting ruleVoting = optRuleVoting.get();

            if (Instant.now().isAfter(ruleVoting.getEndDate().toInstant())) {
                final String url = "http://localhost:8084/association/update-rules";

                RuleVoteResultRequestModel model = new RuleVoteResultRequestModel();
                model.setDate(ruleVoting.getEndDate());
                model.setType(ruleVoting.getType().toString());
                model.setPassed(ruleVoting.passedMotion());
                model.setResult(ruleVoting.getResults());
                model.setAssociationId(ruleVoting.getAssociationId());
                model.setAmendment(ruleVoting.getAmendment());
                model.setAnAmendment(ruleVoting.getType() == VotingType.AMENDMENT);

                String token = requestUtil.authenticateService(username, password);

                HttpHeaders headers = new HttpHeaders();
                headers.set(auth, bearer
                        + token);
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
    public String createElection(VotingType type, int associationId, String userId, String rule, String amendment) {
        Voting voting = votingFactory.createVoting(type, associationId, userId, rule, amendment);
        return "Voting was created for association " + associationId
                + " and will be held on " + voting.getEndDate().toString() + ".";
    }

    /**
     * Returns the candidates of an active board election in a given association.
     *
     * @return a set of User IDs of candidates.
     */
    public Set<String> getCandidates(int associationId) throws IllegalArgumentException {
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
    public String applyForCandidate(String userId, int associationId) {
        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);
        if (optElection.isPresent()) {
            Election election = optElection.get();

            ////Checks if candidate is eligible for election
            //if (!verifyCandidate(userId, associationId)) {
            //throw new IllegalArgumentException("Not eligible to be a candidate.");
            //}

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
     * Verify whether the provided user can be a candidate for the board.
     *
     * @param userId            The user's id.
     * @param associationId     The association id.
     * @return                  True if the user can be a candidate.
     */
    public boolean verifyCandidate(String userId, Integer associationId) {
        final String url = "http://localhost:8084/association/verify-candidate";

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(associationId);
        model.setUserId(userId);

        String token = requestUtil.authenticateService(username,
                password);

        HttpHeaders headers = new HttpHeaders();
        headers.set(auth, bearer
                + token);
        HttpEntity<UserAssociationRequestModel> request = new HttpEntity<>(model, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Boolean> responseEntity = restTemplate.postForEntity(url, request, Boolean.class);

        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return responseEntity.getBody();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Casts a vote for a candidate in the upcoming election, if the date is less than 2 days before the election end.
     *
     * @return a confirmation message.
     */
    public String castElectionVote(String voterId, int associationId, String candidateId) {
        Optional<Election> optElection = electionRepository.findByAssociationId(associationId);
        if (optElection.isPresent()) {
            Election election = optElection.get();

            //Checks if election end date is closer than 2 days, or if the election has ended
            votingDaysCheck(election);

            //Checks if the candidate exists
            candidateExistsCheck(election, candidateId);

            saveElectionVote(election, voterId, candidateId);

            return "The voter with ID " + voterId + " voted for the candidate with ID " + candidateId + ".";

        } else {
            throw new IllegalArgumentException("Association with ID "
                    + associationId + " does not have an active election.");
        }
    }

    /**
     * Checks if election end date is closer than 2 days, or if the election has ended.
     */
    private void votingDaysCheck(Election election) {
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
    }

    /**
     * Checks if the candidate exists.
     */
    private void candidateExistsCheck(Election election, String candidateId) {
        if (!election.getCandidateIds().contains(candidateId)) {
            throw new IllegalArgumentException("Candidate with ID "
                    + candidateId + " does not exist.");
        }
    }

    /**
     * Saves the vote, and removes the previous vote if necessary.
     */
    private void saveElectionVote(Election election, String voterId, String candidateId) {
        //If the voter already voted, remove previous vote
        for (Pair vote : election.getVotes()) {
            if (vote.getFirst().equals(voterId)) {
                election.getVotes().remove(vote);
                break;
            }
        }

        Pair<String, String> vote = Pair.of(voterId, candidateId);
        election.addVote(vote);
        electionRepository.save(election);
    }

    /**
     * Checks whether a certain user is part of the association's council.
     *
     * @param userId            The user's id.
     * @param associationId     The association id.
     * @return                  True if the user is part of the association's council.
     */
    public boolean verifyCouncilMember(String userId, Integer associationId) {
        final String url = "http://localhost:8084/association/verify-council-member";

        UserAssociationRequestModel model = new UserAssociationRequestModel();
        model.setAssociationId(associationId);
        model.setUserId(userId);

        String token = requestUtil.authenticateService(username,
                password);

        HttpHeaders headers = new HttpHeaders();
        headers.set(auth, bearer
                + token);
        HttpEntity<UserAssociationRequestModel> request = new HttpEntity<>(model, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Boolean> responseEntity = restTemplate.postForEntity(url, request, Boolean.class);

        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return responseEntity.getBody();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns whether the proposal does not exist in the existing rules.
     *
     * @param associationId The association this proposal is for.
     * @param proposal      The proposal.
     * @return              True if the proposal is unique, otherwise false
     */
    public boolean verifyProposal(Integer associationId, String proposal) {
        final String url = "http://localhost:8084/association/verify-proposal";

        AssociationProposalRequestModel model = new AssociationProposalRequestModel();
        model.setAssociationId(associationId);
        model.setProposal(proposal);

        String token = requestUtil.authenticateService(username,
                password);

        HttpHeaders headers = new HttpHeaders();
        headers.set(auth, bearer
                + token);
        HttpEntity<AssociationProposalRequestModel> request = new HttpEntity<>(model, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Boolean> responseEntity = restTemplate.postForEntity(url, request, Boolean.class);

        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return responseEntity.getBody();
        } else {
            throw new IllegalArgumentException();
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
    public String proposeRule(VotingType type, Integer associationId, String userId, String rule)
            throws InvalidRuleException, RuleTooLongException {
        if (rule == null) {
            throw new InvalidRuleException("The rule is null.");
        } else if (rule.equals("")) {
            throw new InvalidRuleException("The rule's description is empty.");
        } else if (rule.length() > this.maxRuleLength) {
            throw new RuleTooLongException("The rule description exceeds the maximum length of "
                    + this.maxRuleLength + " characters.");
        } else if (ruleVotingRepository.existsByAssociationIdAndRuleAndType(associationId, rule, type)) {
            throw new InvalidRuleException("The rule is already under evaluation.");
        }

        // //Checks if user is member of council
        // if (!verifyCouncilMember(userId, associationId)) {
        //     throw new IllegalArgumentException("Not a member of the council.");
        // }

        // //Checks if the proposal is unique
        // if (!verifyProposal(associationId, rule)) {
        //     throw new IllegalArgumentException("This rule already exists.");
        // }

        Voting voting = votingFactory.createVoting(type, associationId, userId, rule, null);
        Calendar cal = Calendar.getInstance();
        cal.setTime(voting.getEndDate());
        cal.add(Calendar.DAY_OF_MONTH, -2);
        return "Rule: \"" + rule + "\" has been proposed by: " + userId + "." +  System.lineSeparator()
                + "The vote will be held on: " + cal.getTime();
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
    public String amendmentRule(VotingType type, Integer associationId, String userId, String rule, String amendment)
            throws InvalidRuleException, RuleTooLongException {
        if (amendment == null) {
            throw new InvalidRuleException("The amendment is null.");
        } else if (amendment.length() > this.maxRuleLength) {
            throw new RuleTooLongException("The amendment's description exceeds the maximum length of "
                    + this.maxRuleLength + " characters.");
        } else if (ruleVotingRepository.existsByAssociationIdAndRuleAndType(associationId, rule, type)) {
            throw new InvalidRuleException("The rule is already under evaluation.");
        } else if (ruleVotingRepository.existsByAssociationIdAndAmendment(associationId, amendment)) {
            throw new InvalidRuleException("The amendment already exists in another vote.");
        }

        // //Checks if user is member of council
        // if (!verifyCouncilMember(userId, associationId)) {
        //     throw new IllegalArgumentException("Not a member of the council.");
        // }

        Voting voting = votingFactory.createVoting(type, associationId, userId, rule, amendment);
        Calendar cal = Calendar.getInstance();
        cal.setTime(voting.getEndDate());
        cal.add(Calendar.DAY_OF_MONTH, -2);
        if (amendment.equals("")) {
            return "The user: " + userId + " proposes to remove the rule: \"" + rule + "\"" + System.lineSeparator()
                    + "The vote will be held on: " + cal.getTime();
        } else {
            return "The user: " + userId + " proposes to change the rule: \"" + rule + "\"" + System.lineSeparator()
                    + "to: \"" + amendment + "\"" +  System.lineSeparator() + "The vote will be held on: "
                    + cal.getTime();
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
        Date endDate = optionalRuleVoting.get().getEndDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.DAY_OF_MONTH, -2);

        if (ChronoUnit.SECONDS.between(currentDate.toInstant(), endDate.toInstant()) <= 0) {
            return res + System.lineSeparator() + "Voting has ended." + System.lineSeparator()
                    + "The results can be accessed through the association.";
        } else if (ChronoUnit.SECONDS.between(currentDate.toInstant(), cal.toInstant()) <= 0) {
            cal.setTime(endDate);
            return res + System.lineSeparator() + "You can cast your vote now."
                    + System.lineSeparator() + "The voting will end on: " + cal.getTime();
        } else {
            return res + System.lineSeparator() + "The voting procedure is still in reviewing."
                    + System.lineSeparator() + "The voting will start on: " + cal.getTime();
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
    public String castRuleVote(Long ruleVoteId, String userId, String vote, int associationId) throws InvalidIdException {
        List<String> validVotes = List.of("for", "against", "abstain");
        if (vote == null || !(validVotes.contains(vote))) {
            throw new IllegalArgumentException("The vote is not valid, please pick from: for/against/abstain.");
        } else if (ruleVoteId == null) {
            throw new InvalidIdException("The rule vote id is null.");
        }

        // //Checks if user is member of council
        // if (!verifyCouncilMember(userId, associationId)) {
        //     throw new IllegalArgumentException("Not a member of the council.");
        // }

        Optional<RuleVoting> optionalRuleVoting = ruleVotingRepository.findById(ruleVoteId);
        RuleVoting ruleVoting = optionalRuleVoting
                .orElseThrow(() -> new InvalidIdException("There is no rule vote ongoing with the id: " + ruleVoteId));

        Date currentDate = new Date(System.currentTimeMillis());
        Date ruleVoteEndDate = ruleVoting.getEndDate();
        int daysForVoting = 2;

        if (ChronoUnit.DAYS.between(currentDate.toInstant(), ruleVoteEndDate.toInstant()) >= daysForVoting) {
            throw new IllegalArgumentException("The rule vote is still in reviewing. It is too early to cast a vote.");
        }

        //Checks if rule vote has ended
        if (currentDate.compareTo(ruleVoteEndDate) > 0) {
            throw new IllegalArgumentException("The rule vote has ended.");
        }

        //If the voter already voted, remove previous vote
        for (Pair v : ruleVoting.getVotes()) {
            if (v.getFirst().equals(userId)) {
                ruleVoting.getVotes().remove(v);
                break;
            }
        }

        Pair<String, String> submission = Pair.of(userId, vote);
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

    /**
     * Returns a string representing the ongoing rule votes from the
     * user's association and their current status for that user.
     *
     * @param associationId         The id of the association in which the user is a council member.
     * @param userId                The id of the user.
     * @return                      A string representing the status of all ongoing rule votes.
     * @throws InvalidIdException   Thrown when the association's id is null.
     */
    public String getPendingVotes(Integer associationId, String userId) throws InvalidIdException {
        if (associationId == null) {
            throw new InvalidIdException("The association ID is null.");
        }

        // //Checks if user is member of council
        // if (!verifyCouncilMember(userId, associationId)) {
        //     throw new IllegalArgumentException("Not a member of the council.");
        // }

        List<RuleVoting> pendingVotes = ruleVotingRepository.findAllByAssociationId(associationId);

        if (pendingVotes.isEmpty()) {
            return "There are no ongoing rule votes corresponding to the association ID: " + associationId + ".";
        }

        StringBuilder result = new StringBuilder();

        while (!pendingVotes.isEmpty()) {
            String id = "ID: " + pendingVotes.get(0).getId() + ", ";
            String type = "Type: " + (pendingVotes.get(0).getType() == VotingType.PROPOSAL ? "Proposal" : "Amendment")
                    + ", ";

            String status = "Status: ";
            Date currentDate = new Date(System.currentTimeMillis());
            Date ruleVoteEndDate = pendingVotes.get(0).getEndDate();
            int daysForVoting = 2;

            if (ChronoUnit.DAYS.between(currentDate.toInstant(), ruleVoteEndDate.toInstant()) >= daysForVoting) {
                status += "Reviewing";
                result.append(id);
                result.append(type);
                result.append(status);
                result.append(System.lineSeparator());
            } else {
                if (currentDate.compareTo(ruleVoteEndDate) > 0) {
                    status += "Ended, ";
                } else {
                    status += "Voting, ";
                }
                List<Pair<String, String>> votes = pendingVotes.get(0).getVotes().stream()
                        .filter(x -> x.getFirst().equals(userId))
                        .collect(Collectors.toList());
                String vote = "Your vote: " + (votes.isEmpty() ? "No vote (abstain)" : votes.get(0).getSecond());
                result.append(id);
                result.append(type);
                result.append(status);
                result.append(vote);
                result.append(System.lineSeparator());
            }
            pendingVotes.remove(0);
        }
        return result.toString();
    }
}
