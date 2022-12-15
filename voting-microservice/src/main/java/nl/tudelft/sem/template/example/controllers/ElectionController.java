package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElectionController {
    private final transient AuthManager authManager;

    @Autowired
    public ElectionController(AuthManager authManager) {
        this.authManager = authManager;
    }


}
