package nl.tudelft.sem.template.example.domain.association;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("associationRepository")
public interface AssociationRepository extends JpaRepository<Association, Integer> {

    /*
    find association by id
     */
    Optional<Association> findById(int id);

    /*
    find association by name
     */
    List<Association> findAllByName(String name);

    /*
    get all association name
     */
    List<Association> findBy();

}
