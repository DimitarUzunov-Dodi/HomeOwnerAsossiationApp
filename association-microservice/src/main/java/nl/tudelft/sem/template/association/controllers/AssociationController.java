package nl.tudelft.sem.template.association.controllers;

import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.domain.association.AssociationRepository;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.user.UserService;
import nl.tudelft.sem.template.association.models.RuleVerificationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/association")
public class AssociationController {
    private final transient AuthManager authManager;
    private final transient AssociationService associationService;
    private final transient AssociationRepository associationRepository;
    private final transient UserService userService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager        Spring Security component used to authenticate and authorize the user
     * @param associationService The association service
     * @param userService user service
     */
    @Autowired
    public AssociationController(AuthManager authManager, AssociationService associationService,
                                 AssociationRepository associationRepository, UserService userService) {
        this.authManager = authManager;
        this.associationService = associationService;
        this.associationRepository = associationRepository;
        this.userService = userService;
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

    /**
     * Dummy endpoint for updating the council.
     *
     * @param newCouncil string with the new council to be parsed
     * @return 200 if OK
     */
    @PostMapping("/update-council-dummy")
    public ResponseEntity<String> updateCouncilDummy(String newCouncil) {

        System.out.println(newCouncil);

        return ResponseEntity.ok("Council updated!");
    }

}
