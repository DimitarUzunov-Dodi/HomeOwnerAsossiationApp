package nl.tudelft.sem.template.example.domain.user;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final transient UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * @param userId
     * @return the corresponding memberId
     */
    public Optional<User> getMemberById(String userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * @param userId
     * @param name
     * @return if the userId already exist or not
     * save a member to the repository
     */
    public boolean addUser(String userId, String name) {
        if (userRepository.existsByUserId(userId)) return false;
        userRepository.save(new User(userId, name));
        return true;
    }

}
