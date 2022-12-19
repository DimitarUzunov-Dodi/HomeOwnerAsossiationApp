package nl.tudelft.sem.template.example.domain.association;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;


@Service
public class AssociationService {
    private final transient AssociationRepository associationRepository;

    public AssociationService(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    /** returns the searched association by id.
     *
     * @param associationId id of the association
     * @return Association correspondingly
     */
    public Optional<Association> getAssociation(int associationId) {
        return associationRepository.findById(associationId);
    }

    /** returns the searched association by name.
     *
     * @param name name of the association
     * @return all possible association with the name
     */
    public List<Association> getAssociationByName(String name) {
        return associationRepository.findAllByName(name);
    }

    /** returns all associations in the repository.
     *
     * @return all associations in the repo
     */
    public List<Association> getAllAssociation() {
        return associationRepository.findBy();
    }

    /** add new association to the repository.
     *
     * @param name name of the association
     * @param electionDate election date of the association
     * @param description description of the association
     * @param councilNumber number of council members in the association
     */
    public void addAssociation(String name, Date electionDate, String description, int councilNumber) {
        associationRepository.save(new Association(name, electionDate, description, councilNumber));
    }

}
