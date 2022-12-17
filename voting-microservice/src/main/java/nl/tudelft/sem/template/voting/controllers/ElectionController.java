package nl.tudelft.sem.template.voting.controllers;

import nl.tudelft.sem.template.voting.authentication.AuthManager;
import nl.tudelft.sem.template.voting.domain.ElectionService;
import nl.tudelft.sem.template.voting.models.AssociationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ElectionController {
    private final transient AuthManager authManager;
    private final transient ElectionService electionService;

    @Autowired
    public ElectionController(AuthManager authManager, ElectionService electionService) {
        this.authManager = authManager;
        this.electionService = electionService;
    }

    @PostMapping("/createElection")
    public ResponseEntity<String> createElection(@RequestBody AssociationRequestModel request) throws Exception {
        //We could add the part where we check if it has been 1 year since the last election
        // or just check if another election is ongoing

        //Check for correct associationId will happen in Association before calling this

        ResponseEntity<String> res;
        try {
            int associationId = Integer.parseInt(request.getAssociationId());
            res = ResponseEntity.ok(electionService.createElection(associationId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return res;
    }


}
