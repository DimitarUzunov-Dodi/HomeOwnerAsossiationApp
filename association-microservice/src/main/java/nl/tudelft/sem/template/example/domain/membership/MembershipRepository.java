package nl.tudelft.sem.template.example.domain.membership;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("membershipRepository")
public interface MembershipRepository extends JpaRepository<Membership, Integer> {
    /**existsByUserIdAndAssociationId.
     *
     * @param userId the user id
     * @param associationId the association id
     * @return if a membership exists
     */
    boolean existsByUserIdAndAssociationId(String userId, int associationId);

    /**existsByUserIdAndAssociationIdAndBoard.
     *
     * @param userId the user id condition
     * @param associationId the association id condition
     * @param board the board condition
     * @return the existence over the conditions
     */
    boolean existsByUserIdAndAssociationIdAndBoard(String userId, int associationId, boolean board);

    /**findByAssociationId.
     *
     * @param associationId the association id
     * @return the corresponding memberships
     */
    List<Membership> findByAssociationId(int associationId);

    /**findByAssociationIdAndBoard.
     *
     * @param associationId the association id
     * @param board the board condition
     * @return the corresponding memberships
     */
    List<Membership> findByAssociationIdAndBoard(int associationId, boolean board);

    /**findByUserIdAndAssociationId.
     *
     * @param userId the user id condition
     * @param associationId the association id condition
     * @return the membership with the key conditions
     */
    Optional<Membership> findByUserIdAndAssociationId(String userId, int associationId);


}
