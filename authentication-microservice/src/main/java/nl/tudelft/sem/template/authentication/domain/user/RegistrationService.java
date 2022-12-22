package nl.tudelft.sem.template.authentication.domain.user;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * A DDD service for registering a new user.
 */
@Service
@Transactional
public class RegistrationService {
    private final transient UserRepository userRepository;
    private final transient PasswordHashingService passwordHashingService;

    /**
     * Instantiates a new UserService.
     *
     * @param userRepository  the user repository
     * @param passwordHashingService the password encoder
     */
    public RegistrationService(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    /**
     * Register a new user.
     *
     * @param userId    The UserID of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public AppUser registerUser(UserId userId, Password password) throws Exception {
        if (userId == null || password == null) {
            throw new Exception("NULL_FIELD");
        } else if (checkUserIdIsUnique(userId)) {
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            // Create new account
            AppUser user = new AppUser(userId, hashedPassword);
            userRepository.save(user);

            return user;
        } else {
            throw new UserIdAlreadyInUseException(userId);
        }
    }

    /**
     * Change a user's password.
     *
     * @param userId The UserID of the user
     * @param password The current password of the user
     * @throws Exception if the userId and password don't match
     */
    public void changePassword(UserId userId, Password password) throws Exception {
        Optional<AppUser> tempUser = userRepository.findByUserId(userId);

        if (tempUser.isEmpty()) {
            throw new Exception("CREDENTIALS_NOT_MATCHING");
        }

        if (password == null) {
            throw new Exception("INVALID_PASSWORD");
        }

        HashedPassword hashedPassword = passwordHashingService.hash(password);
        userRepository.changePassword(userId, hashedPassword);
    }

    public boolean checkUserIdIsUnique(UserId userId) {
        return !userRepository.existsByUserId(userId);
    }
}
