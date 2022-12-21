package nl.tudelft.sem.template.authentication.authentication;

import java.util.ArrayList;
import java.util.Optional;
import nl.tudelft.sem.template.authentication.domain.user.AppUser;
import nl.tudelft.sem.template.authentication.domain.user.InvalidFieldException;
import nl.tudelft.sem.template.authentication.domain.user.UserId;
import nl.tudelft.sem.template.authentication.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User details service responsible for retrieving the user from the DB.
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final transient UserRepository userRepository;

    @Autowired
    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user information required for authentication from the DB.
     *
     * @param username The username of the user we want to authenticate
     * @return The authentication user information of that user
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        try {
            Optional<AppUser> optionalUser;
            optionalUser = userRepository.findByUserId(new UserId(username));

            if (optionalUser.isEmpty()) {
                throw new UsernameNotFoundException("User does not exist");
            }

            AppUser user = optionalUser.get();

            return new User(user.getUserId().toString(), user.getPassword().toString(),
                    new ArrayList<>()); // no authorities/roles

        } catch (InvalidFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
