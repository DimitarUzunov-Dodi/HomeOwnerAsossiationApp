package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.association.AssociationService;
import nl.tudelft.sem.template.example.domain.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class DefaultController {

    private final transient AuthManager authManager;

    private final transient AssociationService associationService;

    private final transient UserService userService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager        Spring Security component used to authenticate and authorize the user
     * @param associationService
     * @param userService
     */
    @Autowired
    public DefaultController(AuthManager authManager, AssociationService associationService, UserService userService) {
        this.authManager = authManager;
        this.associationService = associationService;
        this.userService = userService;
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getMemberId());

    }

}
