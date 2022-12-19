package nl.tudelft.sem.template.voting.controllers;

import java.util.Set;
import nl.tudelft.sem.template.voting.authentication.AuthManager;
import nl.tudelft.sem.template.voting.domain.VotingService;
import nl.tudelft.sem.template.voting.models.AssociationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/election")
@RestController
public class ElectionController {
    private final transient AuthManager authManager;
    private final transient VotingService votingService;

    @Autowired
    public ElectionController(AuthManager authManager, VotingService votingService) {
        this.authManager = authManager;
        this.votingService = votingService;
    }


    /**
     * Creates a board election for an association with a given ID.
     *
     * @return a message confirming the creation.
     */
    @PostMapping("/create-election")
    public ResponseEntity<String> createElection(@RequestBody AssociationRequestModel request) throws Exception {
        //We could add the part where we check if it has been 1 year since the last election
        // or just check if another election is ongoing

        //Check for correct associationId will happen in Association before calling this

        try {
            int associationId = request.getAssociationId();
            return ResponseEntity.ok(votingService.createElection(true, associationId, null, null));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Returns the candidates of an active board election in a given association.
     *
     * @return a set of User IDs of candidates.
     */
    @GetMapping("/get-candidates")
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
