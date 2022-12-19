package nl.tudelft.sem.template.example.domain.user;

import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final transient UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /** getter.
     *
     * @param userId the userid
     * @return the corresponding userId
     */
    public Optional<User> getUserById(String userId) {
        return userRepository.findByUserId(userId);
    }

    /** add new user.
     *
     * @param userId the userid
     * @param name the username
     * @return if the userId already exist or not
     */
    public boolean addUser(String userId, String name) {
        if (userRepository.existsByUserId(userId)) {
            return false;
        }
        userRepository.save(new User(userId, name));
        return true;
    }

}
