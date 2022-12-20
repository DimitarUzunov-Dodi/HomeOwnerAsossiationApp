package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.association.AssociationService;
import nl.tudelft.sem.template.example.domain.membership.MembershipService;
import nl.tudelft.sem.template.example.domain.user.UserService;
import nl.tudelft.sem.template.example.models.GetCouncilRequestModel;
import nl.tudelft.sem.template.example.models.IsInCouncilRequestModel;
import nl.tudelft.sem.template.example.models.UpdateCouncilRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController("/council")
public class CouncilController {

    private final transient AuthManager authManager;

    private final transient AssociationService associationService;

    private final transient MembershipService membershipService;

    private final transient UserService userService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager        Spring Security component used to authenticate and authorize the user
     * @param associationService association service
     * @param userService user service
     */
    @Autowired
    public CouncilController(AuthManager authManager, AssociationService associationService, MembershipService membershipService, UserService userService) {
        this.authManager = authManager;
        this.associationService = associationService;
        this.membershipService = membershipService;
        this.userService = userService;
    }

    @PostMapping("/getCouncil")
    public ResponseEntity<Set<Integer>> getCouncil(GetCouncilRequestModel request){
        return ResponseEntity.ok(associationService.getCouncil(request.getAssociationId()));
    }

    @PostMapping("/UpdateCouncil")
    public ResponseEntity<String> updateCouncil(UpdateCouncilRequestModel request) throws IllegalArgumentException{
        for(Integer i : request.getCouncil()){
            if(!membershipService.isInAssociation(Integer.toString(i), request.getAssociationId())){
                throw new IllegalArgumentException("All council members should be part of the association");
            }
        }

        associationService.updateCouncil(request.getAssociationId(), request.getCouncil());

        return ResponseEntity.ok().body("Updated council");
    }

    @PostMapping()
    public ResponseEntity<Boolean> isInCouncil(IsInCouncilRequestModel request){
        // TODO
        return null;
    }
}
