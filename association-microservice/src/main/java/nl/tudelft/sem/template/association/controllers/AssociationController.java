package nl.tudelft.sem.template.association.controllers;

import java.util.List;
import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.membership.FieldNoNullException;
import nl.tudelft.sem.template.association.domain.report.NoSuchRuleException;
import nl.tudelft.sem.template.association.domain.report.ReportInconsistentException;
import nl.tudelft.sem.template.association.domain.report.ReportService;
import nl.tudelft.sem.template.association.domain.user.UserService;
import nl.tudelft.sem.template.association.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/association")
public class AssociationController {
    private final transient AuthManager authManager;
    private final transient AssociationService associationService;
    private final transient AssociationRepository associationRepository;
    private final transient UserService userService;

    private final transient ReportService reportService;


    /**
     * Instantiates a new controller.
     *
     * @param authManager        Spring Security component used to authenticate and authorize the user
     * @param associationService The association service
     * @param userService        user service
     * @param reportService      report service
     */
    @Autowired
    public AssociationController(AuthManager authManager, AssociationService associationService,
                                 AssociationRepository associationRepository, UserService userService,
                                 ReportService reportService) {
        this.authManager = authManager;
        this.associationService = associationService;
        this.associationRepository = associationRepository;
        this.userService = userService;
        this.reportService = reportService;
    }

    /**
     * Checks if the userId is the same as that in the security context.
     * To be used for endpoint security with the userId being that from the request.
     *
     * @param userId provided string
     * @throws ResponseStatusException if userid is null or not the same as in authentication
     */
    public void validateAuthentication(String userId) throws ResponseStatusException {
        if (userId == null || !authManager.validateRequestUser(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
        }
    }

    /**
     * Gets the existing association IDs.
     *
     * @return          A response message with the ids.
     */
    @GetMapping("/get-association-ids")
    public ResponseEntity<List<Integer>> getAssociationIds() {
        try {
            return ResponseEntity.ok(associationService.getAssociationIds());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Gets the information about a specific association.
     *
     * @return   A response message with the information.
     */
    @GetMapping("/get-association")
    public ResponseEntity<String> getAssociationIds(@RequestBody AssociationRequestModel request) {
        try {
            return ResponseEntity.ok(associationService.getAssociationInfo(request.getAssociationId()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Create a new association.
     *
     * @param request   The request body containing association's info.
     * @return          A response message confirming the creation.
     */
    @PostMapping("/create-association")
    public ResponseEntity<String> createAssociation(@RequestBody CreateAssociationRequestModel request) {
        try {
            return ResponseEntity.ok(associationService.createAssociation(request.getName(), request.getCountry(),
                    request.getCity(), request.getDescription(), request.getCouncilNumber()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Join an association.
     *
     * @param request   The request body containing member's info.
     * @return          A response message confirming the join.
     */
    @PostMapping("/join-association")
    public ResponseEntity<String> joinAssociation(@RequestBody JoinAssociationRequestModel request) {
        try {
            validateAuthentication(request.getUserId());
            return ResponseEntity.ok(associationService.joinAssociation(request.getUserId(), request.getAssociationId(),
                    request.getCountry(), request.getCity(), request.getStreet(),
                    request.getHouseNumber(), request.getPostalCode()));
        } catch (ResponseStatusException r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, r.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Leave an association.
     *
     * @param request   The request body containing the user's id and the association's id.
     * @return          A response message confirming the leave.
     */
    @PostMapping("/leave-association")
    public ResponseEntity<String> leaveAssociation(@RequestBody UserAssociationRequestModel request) {
        try {
            validateAuthentication(request.getUserId());
            return ResponseEntity.ok(associationService.leaveAssociation(request.getUserId(), request.getAssociationId()));
        } catch (ResponseStatusException r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, r.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
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
        boolean isEligibleCandidate = associationService.verifyCandidate(request.getUserId(), request.getAssociationId());

        if (isEligibleCandidate) {
            return ResponseEntity.ok("User can apply for a candidate!");
        } else {
            return new ResponseEntity<>("User cannot be a candidate for the council.",
                    HttpStatus.UNAUTHORIZED);
        }
    }

    /**report a certain violation of the rules.
     *
     * @param request the request body for the report
     * @return a response message indicating the result of the report
     */
    @PostMapping("/report")
    public ResponseEntity<String> report(@RequestBody ReportModel request) {
        try {
            validateAuthentication(request.getReporterId());
            reportService.addReport(request.getAssociationId(),
                    request.getReporterId(), request.getViolatorId(), request.getRule());
        } catch (ResponseStatusException r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, r.getMessage());
        } catch (FieldNoNullException f) {
            return new ResponseEntity<>("The arguments of your report should not contain null values!",
                    HttpStatus.BAD_REQUEST);
        } catch (ReportInconsistentException r) {
            return new ResponseEntity<>("Either the reporter or the violator is not in the association!",
                    HttpStatus.BAD_REQUEST);
        } catch (NoSuchRuleException e) {
            return new ResponseEntity<>("No such rule in the association!",
                    HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("Report received!");
    }

}
