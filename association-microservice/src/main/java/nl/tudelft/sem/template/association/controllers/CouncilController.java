package nl.tudelft.sem.template.association.controllers;

import java.util.Set;
import nl.tudelft.sem.template.association.authentication.AuthManager;
import nl.tudelft.sem.template.association.domain.association.AssociationService;
import nl.tudelft.sem.template.association.domain.user.UserService;
import nl.tudelft.sem.template.association.models.GetCouncilRequestModel;
import nl.tudelft.sem.template.association.models.IsInCouncilRequestModel;
import nl.tudelft.sem.template.association.models.UpdateCouncilRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController("/council")
public class CouncilController {

    private final transient AuthManager authManager;

    private final transient AssociationService associationService;

    private final transient UserService userService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager        Spring Security component used to authenticate and authorize the user
     * @param associationService association service
     * @param userService user service
     */
    @Autowired
    public CouncilController(AuthManager authManager, AssociationService associationService, UserService userService) {
        this.authManager = authManager;
        this.associationService = associationService;
        this.userService = userService;
    }

    @PostMapping("/get-council")
    public ResponseEntity<Set<String>> getCouncil(GetCouncilRequestModel request) {
        return ResponseEntity.ok(associationService.getCouncil(request.getAssociationId()));
    }

    @PostMapping("/update-council")
    public ResponseEntity<String> updateCouncil(UpdateCouncilRequestModel request) throws IllegalArgumentException {
        associationService.updateCouncil(request.getCouncil(), request.getAssociationId());
        return ResponseEntity.ok().body("Updated council");
    }

    @PostMapping("/is-in-council")
    public ResponseEntity<Boolean> isInCouncil(IsInCouncilRequestModel request) {
        return ResponseEntity.ok(associationService.verifyCouncilMember(request.getUserId(), request.getAssociationId()));
    }
}
