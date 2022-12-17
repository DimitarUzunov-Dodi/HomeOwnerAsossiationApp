package nl.tudelft.sem.template.authentication.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * A DDD repository for quering and persisting user aggregate roots.
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
    /**
     * Find user by UserID.
     */
    Optional<AppUser> findByUserId(UserId userId);

    /**
     * Check if an existing user already uses a UserID.
     */
    boolean existsByUserId(UserId userId);

    @Modifying
    @Transactional
    @Query("update AppUser user set user.password = ?2 where user.userId = ?1")
    void changePassword(UserId user, HashedPassword hashedPassword);
}
