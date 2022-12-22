package nl.tudelft.sem.template.association.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /*
    return the user according to user id
     */
    Optional<User> findByUserId(String userId);

    /*
    return the user according to id
     */
    Optional<User> findById(int id);

    /*
    the isUser method check if a user exists
     */
    boolean existsByUserId(String userId);

}
