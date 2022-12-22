package nl.tudelft.sem.template.association.controllers;

import java.text.SimpleDateFormat;
import java.util.Locale;
import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.history.Event;
import nl.tudelft.sem.template.association.domain.history.HistoryService;
import nl.tudelft.sem.template.association.domain.membership.FieldNoNullException;
import nl.tudelft.sem.template.association.domain.report.NoSuchRuleException;
import nl.tudelft.sem.template.association.domain.report.ReportInconsistentException;
import nl.tudelft.sem.template.association.domain.report.ReportService;
import nl.tudelft.sem.template.association.domain.user.UserService;
import nl.tudelft.sem.template.association.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/association")
public class AssociationController {
    private final transient AuthManager authManager;
    private final transient AssociationService associationService;
    private final transient AssociationRepository associationRepository;
    private final transient UserService userService;

    private final transient ReportService reportService;

    private final transient HistoryService historyService;

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
                                 ReportService reportService, HistoryService historyService) {
        this.authManager = authManager;
        this.associationService = associationService;
        this.associationRepository = associationRepository;
        this.userService = userService;
        this.reportService = reportService;
        this.historyService = historyService;
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
            return ResponseEntity.ok(associationService.joinAssociation(request.getUserId(), request.getAssociationId(),
                    request.getCountry(), request.getCity(), request.getStreet(),
                    request.getHouseNumber(), request.getPostalCode()));
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
            return ResponseEntity.ok(associationService.leaveAssociation(request.getUserId(), request.getAssociationId()));
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
    public ResponseEntity<String> verifyCouncilMember(@RequestBody RuleVerificationRequestModel request) {
        boolean isMember = associationService.verifyCouncilMember(request.getUserId(), request.getAssociationId());

        if (isMember) {
            return ResponseEntity.ok("Passed council member check!");
        } else {
            return new ResponseEntity<>("You are not a member of this association's council!",
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
            reportService.addReport(request.getAssociationId(),
                    request.getReporterId(), request.getViolatorId(), request.getRule());
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
     * DEPRECATED. To be used as an alternative to the scheduler if that
     * has any issues at some point.
     * Endpoint to request all the council changes for all associations then apply them.
     *
     * @return 200 if ok
     */
    @Deprecated
    @PostMapping("/get-council-results")
    public ResponseEntity<String> getAllCouncilResults() {
        final String url = "http://localhost:8083/election/get-results";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "
                + SecurityContextHolder.getContext().getAuthentication().getCredentials());
        HttpEntity request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ElectionResultRequestListModel> responseEntity =
                restTemplate.postForEntity(url, request, ElectionResultRequestListModel.class);

        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            ElectionResultRequestListModel resultList = responseEntity.getBody();

            System.out.println(resultList.toString());

            // TODO : update council for each association included

            return ResponseEntity.ok("Updates applied!");
        } else {
            return new ResponseEntity<>("Updates failed!",
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * DEPRECATED. To be used as an alternative to the scheduler if that
     * has any issues at some point.
     * Endpoint to request all the council changes for all associations then apply them.
     *
     * @return 200 if OK
     */
    @Deprecated
    @PostMapping("/get-rule-vote-results")
    public ResponseEntity<String> getAllRuleVotingResults() {
        final String url = "http://localhost:8083/rule-voting/get-results";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "
                + SecurityContextHolder.getContext().getAuthentication().getCredentials());
        HttpEntity request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<RuleVoteResultRequestListModel> responseEntity =
                restTemplate.postForEntity(url, request, RuleVoteResultRequestListModel.class);

        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            RuleVoteResultRequestListModel resultList = responseEntity.getBody();

            System.out.println(resultList.toString());

            // TODO : update rules for each association included

            return ResponseEntity.ok("Updates applied!");
        } else {
            return new ResponseEntity<>("Updates failed!",
                    HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<String> updateCouncilDummy(@RequestBody ElectionResultRequestModel request) {
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
     * Also updates the history log for association.
     *
     * @param request request body containing all the info regarding the rule vote
     * @return 200 if OK
     */
    @PostMapping("/update-rules")
    public ResponseEntity<String> updateRulesDummy(@RequestBody RuleVoteResultRequestModel request) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String historyEntry = sdf.format(request.getDate() + " | " + request.getResult());

        System.out.println(historyEntry);

        Event event = new Event(request.getResult(), request.getDate());

        associationService.processRuleVote(request);

        try {
            historyService.addEvent(request.getAssociationId(), event);
        } catch (Exception e) {
            return new ResponseEntity<>("Adding the log in history failed!",
                    HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Rules updated!");
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

}
