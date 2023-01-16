package nl.tudelft.sem.template.association.controllers;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.history.Event;
import nl.tudelft.sem.template.association.domain.history.HistoryService;
import nl.tudelft.sem.template.association.domain.history.Notification;
import nl.tudelft.sem.template.association.domain.location.Address;
import nl.tudelft.sem.template.association.domain.location.Location;
import nl.tudelft.sem.template.association.domain.membership.FieldNoNullException;
import nl.tudelft.sem.template.association.domain.membership.MembershipService;
import nl.tudelft.sem.template.association.domain.report.NoSuchRuleException;
import nl.tudelft.sem.template.association.domain.report.ReportInconsistentException;
import nl.tudelft.sem.template.association.domain.report.ReportService;
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
    private final transient ReportService reportService;
    private final transient HistoryService historyService;
    private final transient MembershipService membershipService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager        Spring Security component used to authenticate and authorize the user
     * @param associationService The association service
     * @param reportService      report service
     * @param historyService     The history service.
     * @param membershipService  The membership service.
     */
    @Autowired
    public AssociationController(AuthManager authManager, AssociationService associationService,
                                 AssociationRepository associationRepository,
                                 ReportService reportService, HistoryService historyService,
                                 MembershipService membershipService) {
        this.authManager = authManager;
        this.associationService = associationService;
        this.associationRepository = associationRepository;
        this.reportService = reportService;
        this.historyService = historyService;
        this.membershipService = membershipService;
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
            Location location = new Location(request.getCountry(), request.getCity());
            return ResponseEntity.ok(associationService.createAssociation(request.getName(), location,
                    request.getDescription(), request.getCouncilNumber()));
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
            Location location = new Location(request.getCountry(), request.getCity());
            Address address = new Address(location, request.getCity(), request.getStreet(), request.getHouseNumber());
            return ResponseEntity.ok(associationService.joinAssociation(request.getUserId(), request.getAssociationId(),
                    address));
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
        boolean isEligibleCandidate = associationService.verifyCandidate(request.getUserId(),
                request.getAssociationId());

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

    /**
     * SCHEDULER related. Endpoint for updating the council.
     * Also updates the history log for association.
     *
     * @param request request body containing all the info regarding the election
     * @return 200 if OK
     */
    @PostMapping("/update-council")
    public ResponseEntity<String> updateCouncil(@RequestBody ElectionResultRequestModel request) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String historyEntry = sdf.format(request.getDate()) + " | " + request.getResult();

        System.out.println(historyEntry);

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
     * SCHEDULER related. Endpoint for updating the rules.
     * Also updates the history log for association and send a notification to all members of the association
     *
     * @param request request body containing all the info regarding the rule vote
     * @return 200 if OK
     */
    @PostMapping("/update-rules")
    public ResponseEntity<String> updateRules(@RequestBody RuleVoteResultRequestModel request) {
        try {
            Event event = new Event(request.getResult(), request.getDate());
            historyService.addEvent(request.getAssociationId(), event);
        } catch (Exception e) {
            return new ResponseEntity<>("Adding the log in history failed!",
                    HttpStatus.BAD_REQUEST);
        }

        String notificationRes = "";
        if (request.isPassed()) {
            try {
                notificationRes = membershipService.createNotificationDescription(request);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        associationService.processRuleVote(request);

        return ResponseEntity.ok("Rules updated" + notificationRes + "!");
    }

    @PostMapping ("get-rules")
    ResponseEntity<String> getAssociationRules(@RequestBody UserAssociationRequestModel request) {
        try {
            String rules = associationService.getAssociationRules(request.getUserId(), request.getAssociationId());
            return ResponseEntity.ok(rules);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("get-history")
    ResponseEntity<String> getAssociationHistory(@RequestBody UserAssociationRequestModel request) {
        try {
            String rules = historyService.getHistoryString(request.getAssociationId());
            return ResponseEntity.ok(rules);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Displays all the notifications of a user for a specific association.
     *
     * @param request   The request body containing the association ID.
     * @return          Return a message displaying notifications.
     */
    @GetMapping("/display-notifications")
    public ResponseEntity<String> displayNotifications(@RequestBody AssociationRequestModel request) {
        try {
            return ResponseEntity.ok(membershipService
                    .displayNotifications(authManager.getUserId(), request.getAssociationId()));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Dismisses all read notifications of a user for a certain association.
     *
     * @param request    The request body containing the association ID.
     * @return           Return a message indicating the status of dismissal.
     */
    @PostMapping("/dismiss-notifications")
    public ResponseEntity<String> dismissNotifications(@RequestBody AssociationRequestModel request) {
        try {
            return ResponseEntity.ok(membershipService
                    .dismissNotifications(authManager.getUserId(), request.getAssociationId()));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


}
