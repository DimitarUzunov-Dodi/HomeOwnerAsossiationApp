package nl.tudelft.sem.template.association.controllers;

import java.io.IOException;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.template.association.domain.rules.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController("/rules")
public class RuleController {

    private final transient RuleService ruleService;

    /**
     * Instantiates a new controller.
     *
     * @param ruleService rule logical service
     */
    @Autowired
    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    //TODO test that if something else that ok is returned, it still workes

    @PostMapping("/vote")
    public ResponseEntity<String> voteOnRule(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        return ruleService.voteOnRule(request);
    }

    @PostMapping("/propose")
    public ResponseEntity<String> proposeRule(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        return ruleService.proposeRule(request);
    }

    @PostMapping("/amend")
    public ResponseEntity<String> amendRule(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        return ruleService.amendRule(request);
    }

    @GetMapping("/get")
    public ResponseEntity<String> getRules(HttpServletRequest request)
            throws IOException, NoSuchElementException, IllegalArgumentException {
        return ruleService.getRules(request);
    }
}
