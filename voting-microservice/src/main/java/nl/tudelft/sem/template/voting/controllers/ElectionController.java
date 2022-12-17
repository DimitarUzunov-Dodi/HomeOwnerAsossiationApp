package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElectionController {
    private final transient AuthManager authManager;
    private final transient ElectionService electionService;

    @Autowired
    public ElectionController(AuthManager authManager, ElectionService electionService) {
        this.authManager = authManager;
        this.electionService = electionService;
    }

    @GetMapping("/createElection")
    public ResponseEntity<String> createElection(int associationId) {
        //We could add the part where we check if it has been 1 year since the last election
        // or just check if another election is ongoing

        //Check for associationId will happen in Association before calling this

        return ResponseEntity.ok(electionService.createElection(associationId));
    }


}
