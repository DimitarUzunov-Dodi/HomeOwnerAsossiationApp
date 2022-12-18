package nl.tudelft.sem.template.example.domain.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("membershipRepository")
public interface MembershipRepository extends JpaRepository<Membership, Integer> {

    boolean existsByUserIdAndAssociationId(String userId, int associationId);

    boolean existsByUserIdAndAssociationIdAndBoard(String userId, int associationId, boolean board);

    List<Membership> findByAssociationId(int associationId);

    List<Membership> findByAssociationIdAndBoard(int associationId, boolean board);

    Optional<Membership> findByUserIdAndAssociationId(String userId, int associationId);


}
