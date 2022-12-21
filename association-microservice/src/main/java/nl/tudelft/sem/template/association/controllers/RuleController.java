package nl.tudelft.sem.template.association.controllers;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.rules.RuleService;
import nl.tudelft.sem.template.association.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/rules")
public class RuleController {

    private final transient AuthManager authManager;

    private final transient AssociationService associationService;

    private final transient UserService userService;

    private final transient RuleService ruleService;

    /**
     * Instantiates a new controller.
     *
     *  @param authManager        Spring Security component used to authenticate and authorize the user
     * @param associationService association service
     * @param userService user service
     * @param ruleService rule logical service
     */
    @Autowired
    public RuleController(AuthManager authManager,
                          AssociationService associationService,
                          UserService userService,
                          RuleService ruleService) {
        this.authManager = authManager;
        this.associationService = associationService;
        this.userService = userService;
        this.ruleService = ruleService;
    }

    @PostMapping("/vote")
    public ResponseEntity<Integer> voteOnRule(HttpServletRequest request) {
        // TODO
        return null;
    }

    @PostMapping("/propose")
    public ResponseEntity<Integer> proposeRule(HttpServletRequest request) {
        // TODO
        return null;
    }

    @PostMapping("/amend")
    public ResponseEntity<Integer> amendRule(HttpServletRequest request) {
        // TODO
        return null;
    }

    @GetMapping("/get")
    public ResponseEntity<Integer> getRules(HttpServletRequest request) {
        // TODO
        return null;
    }
}
