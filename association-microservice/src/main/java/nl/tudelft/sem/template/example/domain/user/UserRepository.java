package nl.tudelft.sem.template.example.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Integer> {
    /*
    return the user according to user id
     */
    Optional<User> findByUserId(String userId);

    /*
    the isUser method check if a user exists
     */
    boolean existsByUserId(String userId);

}
