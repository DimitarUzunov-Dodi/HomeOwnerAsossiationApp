package nl.tudelft.sem.template.example.domain.activity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** repository for activities with methods to extract what is needed.
 *
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    /*
    returns activity based on id
     */
    Optional<Activity> findByActivityId(int eventId);


    /*
    returns the notice board content
     */
    List<Activity> findAllByAssociationId(int associationId);
}
