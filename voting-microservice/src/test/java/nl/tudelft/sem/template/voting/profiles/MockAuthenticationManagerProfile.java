package nl.tudelft.sem.template.voting.profiles;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.voting.authentication.AuthManager;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * A configuration profile to allow injection of a mock AuthenticationManager.
 * A configuration can be used to define beans to be used during injection.
 * When this profile is active spring dependency injection will use this class to look for bean methods.
 * It will then prioritise these beans due to their @Primary tag.
 * In this case we return a mock to allow for more testability.
 * .
 * The profile tag will give a name to this configuration.
 * With the tag applied the profile will be inactive by default unless activated.
 * When the profile is active its bean will be used when looking for Beans to auto-inject.
 */
@Profile("mockAuthenticationManager")
@Configuration
public class MockAuthenticationManagerProfile {

    /**
     * Mocks the AuthenticationManager.
     *
     * @return A mocked AuthenticationManager.
     */
    @Bean
    @Primary  // marks this bean as the first bean to use when trying to inject an AuthenticationManager
    public AuthManager getMockAuthenticationManager() {
        AuthManager mockAuthManager = Mockito.mock(AuthManager.class);
        when(mockAuthManager.validateRequestUser(anyString())).thenReturn(true);
        return mockAuthManager;
    }
}

