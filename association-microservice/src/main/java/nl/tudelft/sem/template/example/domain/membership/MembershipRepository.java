package nl.tudelft.sem.template.example.domain.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("membershipRepository")
public interface MembershipRepository extends JpaRepository<Membership, Integer> {
    /**
     *
     * @param userId
     * @param associationId
     * @return if a membership exists
     */
    boolean existsByUserIdAndAssociationId(String userId, int associationId);

    /**
     *
     * @param userId
     * @param associationId
     * @param board
     * @return
     */
    boolean existsByUserIdAndAssociationIdAndBoard(String userId, int associationId, boolean board);

    /**
     *
     * @param associationId
     * @return
     */
    List<Membership> findByAssociationId(int associationId);

    /**
     *
     * @param associationId
     * @param board
     * @return
     */
    List<Membership> findByAssociationIdAndBoard(int associationId, boolean board);

    /**
     *
     * @param userId
     * @param associationId
     * @return
     */
    Optional<Membership> findByUserIdAndAssociationId(String userId, int associationId);


}
