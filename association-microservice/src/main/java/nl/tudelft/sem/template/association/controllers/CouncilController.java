package nl.tudelft.sem.template.association.controllers;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;
import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.history.Event;
import nl.tudelft.sem.template.association.domain.history.HistoryService;
import nl.tudelft.sem.template.association.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/association")
public class CouncilController {

    private final transient AuthManager authManager;
    private final transient AssociationService associationService;
    private final transient HistoryService historyService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager        Spring Security component used to authenticate and authorize the user
     * @param associationService association service
     */
    @Autowired
    public CouncilController(AuthManager authManager, AssociationService associationService, HistoryService historyService) {
        this.authManager = authManager;
        this.associationService = associationService;
        this.historyService = historyService;
    }

    /**
     * Verify whether the provided user is part of the provided association.
     *
     * @param request   The request body containing the user's id and the association's id.
     * @return          A response message in the form of string indicating the result of the verification.
     */
    @PostMapping("/verify-council-member")
    public ResponseEntity<String> verifyCouncilMember(@RequestBody UserAssociationRequestModel request) {
        boolean isMember = associationService.verifyCouncilMember(request.getUserId(), request.getAssociationId());

        if (isMember) {
            return ResponseEntity.ok("User passed council member check!");
        } else {
            return new ResponseEntity<>("User is not a member of this association's council!",
                    HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Verify whether the provided user can be a candidate for the board.
     *
     * @param request   The request body containing the user's id and the association's id.
     * @return          A response message in the form of string indicating the result of the verification.
     */
    @PostMapping("/verify-candidate")
    public ResponseEntity<String> verifyCandidate(@RequestBody UserAssociationRequestModel request) {
        boolean isEligibleCandidate = associationService.verifyCandidate(request.getUserId(),
                request.getAssociationId());

        if (isEligibleCandidate) {
            return ResponseEntity.ok("User can apply for a candidate!");
        } else {
            return new ResponseEntity<>("User cannot be a candidate for the council.",
                    HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * SCHEDULER related. Endpoint for updating the council.
     * Also updates the history log for association.
     *
     * @param request request body containing all the info regarding the election
     * @return 200 if OK
     */
    @PostMapping("/update-council")
    public ResponseEntity<String> updateCouncil(@RequestBody ElectionResultRequestModel request) {
        Event event = new Event(request.getResult(), request.getDate());

        associationService.processElection(request);

        try {
            historyService.addEvent(request.getAssociationId(), event);
        } catch (Exception e) {
            return new ResponseEntity<>("Adding the log in history failed!",
                    HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Council updated!");
    }

    /**
     * Returns true if the user is eligible board candidate.
     *
     * @return   A response message with the boolean.
     */
    @PostMapping("/verify-proposal")
    public ResponseEntity<Boolean> verifyProposal(@RequestBody AssociationProposalRequestModel request) {
        try {
            return ResponseEntity.ok(associationService.verifyProposal(request.getAssociationId(),
                    request.getProposal()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not verify the proposal.");
        }
    }
}
