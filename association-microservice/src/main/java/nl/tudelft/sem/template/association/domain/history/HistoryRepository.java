package nl.tudelft.sem.template.association.domain.history;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("historyRepository")
public interface HistoryRepository extends JpaRepository<History, Integer> {
    /**
     * Find history by associationID.
     */
    Optional<History> findByAssociationId(int associationId);
}
