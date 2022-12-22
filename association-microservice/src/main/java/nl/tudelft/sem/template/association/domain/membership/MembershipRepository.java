package nl.tudelft.sem.template.association.domain.membership;

import java.util.Date;
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

    /**findByAssociationId.
     *
     * @param associationId the association id
     * @return the corresponding memberships
     */
    List<Membership> findByAssociationId(int associationId);

    /**findByUserIdAndAssociationId.
     *
     * @param userId the user id condition
     * @param associationId the association id condition
     * @return the membership with the key conditions
     */
    Optional<Membership> findByUserIdAndAssociationId(String userId, int associationId);

    /**findAllByUserId.
     *
     * @param userId the user id
     * @return the corresponding memberships
     */
    List<Membership> findAllByUserId(String userId);

    /**findByUserIdAndAssociationIdAndLeaveDate.
     *
     * @param userId the user id condition
     * @param associationId the association id condition
     * @param leaveDate the leave date
     * @return the membership with the key conditions
     */
    Optional<Membership> findByUserIdAndAssociationIdAndLeaveDate(String userId, int associationId, Date leaveDate);
}
