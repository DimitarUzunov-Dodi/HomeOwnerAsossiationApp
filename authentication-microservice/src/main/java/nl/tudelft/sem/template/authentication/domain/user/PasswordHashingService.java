package nl.tudelft.sem.template.authentication.domain.user;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * A DDD service for hashing passwords.
 */
public class PasswordHashingService {

    private final transient PasswordEncoder encoder;

    public PasswordHashingService(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    /**
     * Hashes a password.
     *
     * @param password The password to be hashed
     * @return The encoded password
     * @throws Exception if the password is null or empty
     */
    public HashedPassword hash(Password password) throws Exception {
        if (password != null && !password.toString().isEmpty()) {
            return new HashedPassword(encoder.encode(password.toString()));
        } else {
            throw new Exception("INVALID_PASS");
        }
    }
}
