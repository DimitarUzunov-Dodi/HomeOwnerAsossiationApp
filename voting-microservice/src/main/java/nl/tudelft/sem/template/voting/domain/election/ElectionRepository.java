package nl.tudelft.sem.template.voting.domain.election;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    /**
     * Find election by AssociationID.
     */
    Optional<Election> findByAssociationId(int associationId);

    Optional<Election> findFirstByOrderByEndDateAsc();

    @Query("SELECT election from Election election where election.endDate < :date")
    Optional<Collection<Election>> findAllFinishedElections(Date date);
}
