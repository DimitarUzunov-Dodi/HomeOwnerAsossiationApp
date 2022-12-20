package nl.tudelft.sem.template.voting.controllers;

import java.util.Set;
import nl.tudelft.sem.template.voting.authentication.AuthManager;
import nl.tudelft.sem.template.voting.domain.VotingService;
import nl.tudelft.sem.template.voting.models.AssociationRequestModel;
import nl.tudelft.sem.template.voting.models.RuleAmendmentRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class VotingController {

    private final transient AuthManager authManager;
    private final transient VotingService votingService;

    /**
     * Instantiates new voting controller.
     *
     * @param authManager   Spring Security component used to authenticate and authorize the user.
     * @param votingService The voting service.
     */
    @Autowired
    public VotingController(AuthManager authManager, VotingService votingService) {
        this.authManager = authManager;
        this.votingService = votingService;
    }

    /**
     * Creates a board election for an association with a given ID.
     *
     * @return a message confirming the creation.
     */
    @PostMapping("/election/create-election")
    public ResponseEntity<String> createElection(@RequestBody AssociationRequestModel request) throws Exception {
        //We could add the part where we check if it has been 1 year since the last election
        // or just check if another election is ongoing

        //Check for correct associationId will happen in Association before calling this

        try {
            int associationId = request.getAssociationId();
            return ResponseEntity.ok(votingService.createElection("Election", associationId, null, null, null));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
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
            return ResponseEntity.ok(votingService
                    .amendmentRule("Amendment", request.getAssociationId(), request.getUserId(),
                            request.getRule(), request.getAmendment()));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Returns the candidates of an active board election in a given association.
     *
     * @return a set of User IDs of candidates.
     */
    @GetMapping("/election/get-candidates")
    public ResponseEntity<Set<Integer>> getCandidates(@RequestBody AssociationRequestModel request) throws Exception {
        //Check for correct associationId will happen in Association before calling this

        try {
            int associationId = request.getAssociationId();
            return ResponseEntity.ok(votingService.getCandidates(associationId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
