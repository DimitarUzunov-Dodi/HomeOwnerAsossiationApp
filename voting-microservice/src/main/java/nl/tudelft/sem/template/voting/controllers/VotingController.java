package nl.tudelft.sem.template.voting.controllers;

import java.util.Set;
import nl.tudelft.sem.template.voting.authentication.AuthManager;
import nl.tudelft.sem.template.voting.domain.VotingService;
import nl.tudelft.sem.template.voting.models.AssociationRequestModel;
import nl.tudelft.sem.template.voting.models.UserAssociationRequestModel;
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
     * Returns the candidates of an active board election in a given association.
     *
     * @return a set of User IDs of candidates.
     */
    @GetMapping("/election/get-candidates")
    public ResponseEntity<Set<Integer>> getCandidates(@RequestBody AssociationRequestModel request) throws Exception {
        try {
            int associationId = request.getAssociationId();
            return ResponseEntity.ok(votingService.getCandidates(associationId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Registers a candidate for the upcoming election, if the date is 2 or more days before the election.
     *
     * @return a confirmation message.
     */
    @PostMapping("/election/apply-for-candidate")
    public ResponseEntity<String> applyForCandidate(@RequestBody UserAssociationRequestModel request) throws Exception {
        try {
            int userId = request.getUserId();
            int associationId = request.getAssociationId();
            return ResponseEntity.ok(votingService.applyForCandidate(userId, associationId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
