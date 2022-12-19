package nl.tudelft.sem.template.voting.authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Authentication Manager.
 */
@Component
public class AuthManager {
    /**
     * Interfaces with spring security to get the name of the user in the current context.
     *
     * @return The name of the user.
     */
    public String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
