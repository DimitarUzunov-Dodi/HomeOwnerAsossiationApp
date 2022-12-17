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
     * @param memberId    The MemberID of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public AppUser registerUser(MemberId memberId, Password password) throws Exception {

        if (checkMemberIdIsUnique(memberId)) {
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            // Create new account
            AppUser user = new AppUser(memberId, hashedPassword);
            userRepository.save(user);

            return user;
        } else {
            throw new MemberIdAlreadyInUseException(memberId);
        }
    }

    /**
     * Change a user's password.
     *
     * @param memberId The MemberID of the user
     * @param password The current password of the user
     * @throws Exception if the memberId and password don't match
     */
    public void changePassword(MemberId memberId, Password password) throws Exception {
        Optional<AppUser> tempUser = userRepository.findByMemberId(memberId);

        if (tempUser.isEmpty()) {
            throw new Exception("Credentials don't match existing user");
        }

        HashedPassword hashedPassword = passwordHashingService.hash(password);
        userRepository.changePassword(memberId, hashedPassword);
    }

    public boolean checkMemberIdIsUnique(MemberId memberId) {
        return !userRepository.existsByMemberId(memberId);
    }
}
