package nl.tudelft.sem.template.authentication.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting user aggregate roots.
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
    /**
     * Find user by MemberID.
     */
    Optional<AppUser> findByMemberId(MemberId memberId);

    /**
     * Check if an existing user already uses a MemberID.
     */
    boolean existsByMemberId(MemberId memberId);
}
