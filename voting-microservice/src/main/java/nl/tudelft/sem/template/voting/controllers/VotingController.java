package nl.tudelft.sem.template.voting.controllers;

import java.util.Set;
import nl.tudelft.sem.template.voting.domain.VotingService;
import nl.tudelft.sem.template.voting.domain.VotingType;
import nl.tudelft.sem.template.voting.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class VotingController {

    private final transient VotingService votingService;

    /**
     * Instantiates new voting controller.
     *
     * @param votingService The voting service.
     */
    @Autowired
    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    /**
     * Checks if the userId is the same as that in the security context.
     * To be used for endpoint security with the userId being that from the request.
     *
     * @param userId provided string
     * @throws ResponseStatusException if userid is null or not the same as in authentication
     */
    public void validateAuthentication(String userId) throws ResponseStatusException {
        if (userId == null || !SecurityContextHolder.getContext().getAuthentication().getName().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
        }
    }

    /**
     * Creates a board election for an association with a given ID.
     *
     * @return a message confirming the creation.
     */
    @PostMapping("/election/create-election")
    public ResponseEntity<String> createElection(@RequestBody AssociationRequestModel request)
            throws ResponseStatusException {
        int associationId = request.getAssociationId();
        return ResponseEntity.ok(votingService
                .createElection(VotingType.ELECTION, associationId, null, null, null));

    }

    /**
     * Creates a new rule vote for the proposed rule.
     *
     * @param request   The request body containing the association's id, proposer's id and the rule to propose.
     * @return          A message confirming the creation.
     */
    @PostMapping("/rule-voting/propose-rule")
    public ResponseEntity<String> proposeRule(@RequestBody RuleProposalRequestModel request) {
        try {
            validateAuthentication(request.getUserId());
            return ResponseEntity.ok(votingService
                    .proposeRule(VotingType.PROPOSAL, request.getAssociationId(),
                            request.getUserId(), request.getRule()));
        } catch (ResponseStatusException r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, r.getMessage());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Creates a new rule vote for the proposed amendment.
     *
     * @param request   The request body containing the association's id, proposer's id, rule and amendment to propose.
     * @return          A message confirming the creation.
     */
    @PostMapping("/rule-voting/amend-rule")
    public ResponseEntity<String> amendRule(@RequestBody RuleAmendmentRequestModel request) {
        try {
            validateAuthentication(request.getUserId());
            return ResponseEntity.ok(votingService
                    .amendmentRule(VotingType.AMENDMENT, request.getAssociationId(), request.getUserId(),
                            request.getRule(), request.getAmendment()));
        } catch (ResponseStatusException r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, r.getMessage());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Returns a string representation of the rule vote object corresponding to the id.
     *
     * @param request   The request body containing the rule vote id.
     * @return          The string representation of the rule vote object.
     */
    @GetMapping("/rule-voting/get-rule-vote")
    public ResponseEntity<String> getRuleVote(@RequestBody RuleVotingRequestModel request) {
        try {
            return ResponseEntity.ok(votingService.getRuleVoting(request.getRuleVotingId()));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Returns the candidates of an active board election in a given association.
     *
     * @return a set of User IDs of candidates.
     */
    @GetMapping("/election/get-candidates")
    public ResponseEntity<Set<String>> getCandidates(@RequestBody AssociationRequestModel request)
            throws ResponseStatusException {
        try {
            int associationId = request.getAssociationId();
            return ResponseEntity.ok(votingService.getCandidates(associationId));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Registers a candidate for the upcoming election, if the date is 2 or more days before the election.
     *
     * @return a confirmation message.
     */
    @PostMapping("/election/apply-for-candidate")
    public ResponseEntity<String> applyForCandidate(@RequestBody UserAssociationRequestModel request) {
        try {
            validateAuthentication(request.getUserId());
            String userId = request.getUserId();
            int associationId = request.getAssociationId();
            return ResponseEntity.ok(votingService.applyForCandidate(userId, associationId));
        } catch (ResponseStatusException r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, r.getMessage());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Casts a vote for a candidate in the upcoming election, if the date is less than 2 days before the election end.
     *
     * @return a confirmation message.
     */
    @PostMapping("/election/cast-vote")
    public ResponseEntity<String> castElectionVote(@RequestBody ElectionVoteRequestModel request)
            throws ResponseStatusException {
        try {
            validateAuthentication(request.getVoterId());
            String voterId = request.getVoterId();
            int associationId = request.getAssociationId();
            String candidateId = request.getCandidateId();
            return ResponseEntity.ok(votingService.castElectionVote(voterId, associationId, candidateId));
        } catch (ResponseStatusException r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, r.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Casts a vote for a rule in a rule vote, if the reviewing period has passed.
     *
     * @return a confirmation message.
     */
    @PostMapping("/rule-voting/cast-vote")
    public ResponseEntity<String> castRuleVotingVote(@RequestBody RuleVoteRequestModel request) {
        try {
            validateAuthentication(request.getUserId());
            return ResponseEntity.ok(votingService
                    .castRuleVote(request.getRuleVoteId(), request.getUserId(), request.getVote(),
                            request.getAssociationId()));
        } catch (ResponseStatusException r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, r.getMessage());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Returns a string representing the ongoing rule votes from the
     * user's association and their current status for that user.
     *
     * @param request   The request body containing the user's id and the association in which they are a board member.
     * @return          A string representing the status of all ongoing rule votes.
     */
    @GetMapping("/rule-voting/get-pending-votes")
    public ResponseEntity<String> getPendingVotes(@RequestBody UserAssociationRequestModel request) {
        try {
            validateAuthentication(request.getUserId());
            return ResponseEntity.ok(votingService.getPendingVotes(request.getAssociationId(), request.getUserId()));
        } catch (ResponseStatusException r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, r.getMessage());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
