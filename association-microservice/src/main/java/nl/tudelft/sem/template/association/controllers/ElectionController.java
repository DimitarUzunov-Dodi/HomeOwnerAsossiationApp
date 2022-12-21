package nl.tudelft.sem.template.association.controllers;

import java.io.IOException;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.association.domain.election.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;


@Controller("/election")
public class ElectionController {

    public final transient ElectionService electionService;

    @Autowired
    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    // TODO: add the exceptions to the advisor

    @PostMapping("/create-election")
    public ResponseEntity<String> createElection(HttpServletRequest request) throws IOException {
        return electionService.createElection(request);
    }

    @PostMapping("/get-candidates")
    public ResponseEntity<String> getCandidates(HttpServletRequest request) throws IOException {
        return electionService.getCandidates(request);
    }

    @PostMapping("/apply-for-candidate")
    public ResponseEntity<String> applyForCandidate(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        return electionService.applyForCandidate(request);
    }

    @PostMapping("/cast-vote")
    public ResponseEntity<String> castVote(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        return electionService.castVote(request);
    }
}
