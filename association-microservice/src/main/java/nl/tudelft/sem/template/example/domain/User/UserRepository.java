package nl.tudelft.sem.template.example.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("memberRepository")
public interface UserRepository extends JpaRepository<User,Integer> {
    /*
    return the user according to user id
     */
    Optional<User> findByUserId(String userId);

    /*
    the isUser method check if a user exists
     */
    boolean existsByUserId(String userId);

}
