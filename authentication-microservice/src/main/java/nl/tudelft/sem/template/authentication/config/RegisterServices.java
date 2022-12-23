package nl.tudelft.sem.template.authentication.config;

import nl.tudelft.sem.template.authentication.domain.user.Password;
import nl.tudelft.sem.template.authentication.domain.user.RegistrationService;
import nl.tudelft.sem.template.authentication.domain.user.UserId;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RegisterServices {
    private transient RegistrationService registrationService;

    /**
     * Registers the services, so they can create a token for server to server communication.
     *
     * @throws Exception    Thrown when registering raises an exception.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void registerServices() throws Exception {
        if (registrationService.checkUserIdIsUnique(new UserId("VotingService"))) {
            registrationService.registerUser(new UserId("VotingService"), new Password("SuperSecretPassword"));
        }
        if (registrationService.checkUserIdIsUnique(new UserId("AssociationService"))) {
            registrationService.registerUser(new UserId("AssociationService"), new Password("SuperSecretPassword"));
        }
    }
}

