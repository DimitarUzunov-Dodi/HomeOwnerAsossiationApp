package nl.tudelft.sem.template.voting.domain;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import nl.tudelft.sem.template.voting.domain.election.Election;
import nl.tudelft.sem.template.voting.domain.election.ElectionRepository;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVoting;
import nl.tudelft.sem.template.voting.domain.rulevoting.RuleVotingRepository;
import nl.tudelft.sem.template.voting.models.AssociationProposalRequestModel;
import nl.tudelft.sem.template.voting.models.ElectionResultRequestModel;
import nl.tudelft.sem.template.voting.models.RuleVoteResultRequestModel;
import nl.tudelft.sem.template.voting.models.UserAssociationRequestModel;
import nl.tudelft.sem.template.voting.utils.RequestUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

public class VotingAssociationCommunication {

    private final transient String username = "VotingService";
    private final transient String password = "SuperSecretPassword";
    private final transient String auth = "Authorization";
    private final transient String bearer = "Bearer";
    public final transient VotingFactory votingFactory;
    private final transient RequestUtil requestUtil;

    private final transient ElectionRepository electionRepository;
    private final transient RuleVotingRepository ruleVotingRepository;

    /**
     * Instantiates a VotingAssociationCommunication object which provides methods to the VotingService.
     */
    public VotingAssociationCommunication(ElectionRepository electionRepository,
                                          RuleVotingRepository ruleVotingRepository, RequestUtil requestUtil) {
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
}
