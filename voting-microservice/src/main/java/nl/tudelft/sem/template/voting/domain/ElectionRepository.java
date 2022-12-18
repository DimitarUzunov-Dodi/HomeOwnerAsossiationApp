package nl.tudelft.sem.template.voting.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    /**
     * Find election by AssociationID.
     */
    Optional<Election> findByAssociationId(int associationId);
}
