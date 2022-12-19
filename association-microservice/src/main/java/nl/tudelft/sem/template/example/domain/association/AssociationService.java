package nl.tudelft.sem.template.example.domain.association;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AssociationService {
    private final transient AssociationRepository associationRepository;

    public AssociationService(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    /**
     * @param associationId
     * @return Association correspondingly
     */
    public Optional<Association> getAssociation(int associationId) {
        return associationRepository.findById(associationId);
    }

    /**
     * @param name
     * @return all possible association with the name
     */
    public List<Association> getAssociationByName(String name) {
        return associationRepository.findAllByName(name);
    }

    /**
     * @return all associations in the repo
     */
    public List<Association> getAllAssociation() {
        return associationRepository.findBy();
    }

    /**
     * @param name
     * @param electionDate
     * @param description
     * @param councilNumber add new association to the repo
     */
    public void addAssociation(String name, Date electionDate, String description, int councilNumber) {
        associationRepository.save(new Association(name, electionDate, description, councilNumber));
    }

    /**
     *
     * @param id
     * @param electionDate
     * @param description
     */
    public void updateAssociation(int id, Date electionDate, String description) {
        Association association = this.getAssociation(id).get();
        association.setDescription(description);
        association.setElectionDate(electionDate);
        associationRepository.save(association);
    }

}
