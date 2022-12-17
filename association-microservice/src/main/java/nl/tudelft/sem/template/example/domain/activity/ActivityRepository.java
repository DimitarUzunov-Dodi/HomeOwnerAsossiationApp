package nl.tudelft.sem.template.example.domain.activity;

import nl.tudelft.sem.template.example.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {

    /*
    returns activity based on id
     */
    Optional<Activity> findByActivityId(int eventId);


    /*
    returns the notice board content
     */
    List<Activity> findAllByAssociationId(int associationId);
}
