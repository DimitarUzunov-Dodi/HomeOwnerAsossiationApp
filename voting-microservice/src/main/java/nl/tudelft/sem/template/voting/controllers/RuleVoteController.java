package nl.tudelft.sem.template.voting.controllers;

import nl.tudelft.sem.template.voting.authentication.AuthManager;
import nl.tudelft.sem.template.voting.domain.RuleVotingService;
import nl.tudelft.sem.template.voting.models.RuleVerificationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rule-voting")
public class RuleVoteController {
    private final transient AuthManager authManager;
    private final transient RuleVotingService ruleVotingService;

    @Autowired
    public RuleVoteController(AuthManager authManager, RuleVotingService ruleVotingService) {
        this.authManager = authManager;
        this.ruleVotingService = ruleVotingService;
    }

    /**
     * Verify whether the provided user is part of the provided council.
     *
     * @param request   The request body containing the user's id and the list of council members.
     * @return          A response message in the form of string indicating the result of the verification.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody RuleVerificationRequestModel request) {
        boolean isMember = ruleVotingService.verify(request.getUserId(), request.getCouncilMembers());

        if (isMember) {
            return ResponseEntity.ok("Passed council member check!");
        } else {
            return new ResponseEntity<>("You are not a member of this association's council!",
                    HttpStatus.UNAUTHORIZED);
        }
    }
}
