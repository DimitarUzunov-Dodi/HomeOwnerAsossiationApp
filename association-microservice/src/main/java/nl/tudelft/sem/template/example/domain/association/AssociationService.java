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

    /**getter.
     *
     * @param associationId the id of the association
     * @return Association correspondingly
     */
    public Optional<Association> getAssociation(int associationId) {
        return associationRepository.findById(associationId);
    }

    /**getter.
     *
     * @param name the name of the association
     * @return all possible association with the name
     */
    public List<Association> getAssociationByName(String name) {
        return associationRepository.findAllByName(name);
    }

    /**getter.
     *
     * @return all associations in the repo
     */
    public List<Association> getAllAssociation() {
        return associationRepository.findBy();
    }

    /**add new associaiton.
     *
     * @param name name of the association
     * @param electionDate election date of the association
     * @param description description of the association
     * @param councilNumber add new association to the repo
     */
    public void addAssociation(String name, Date electionDate, String description, int councilNumber) {
        associationRepository.save(new Association(name, electionDate, description, councilNumber));
    }

    /**update the association.
     *
     * @param id for finding the association to update
     * @param electionDate update the election date
     * @param description update the description
     */
    public void updateAssociation(int id, Date electionDate, String description) {
        Association association = this.getAssociation(id).get();
        association.setDescription(description);
        association.setElectionDate(electionDate);
        associationRepository.save(association);
    }

}
