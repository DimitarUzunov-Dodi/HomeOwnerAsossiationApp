package nl.tudelft.sem.template.association.domain.association;

import java.util.*;
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
    public Association getAssociationById(int associationId) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isPresent()) {
            return optionalAssociation.get();
        } else {
            throw new IllegalArgumentException("Association with ID "
                    + associationId + " does not exist.");
        }
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
     * @param associationId the association
     * @return council of the association
     */
    public Set<Integer> getCouncil(int associationId) {
        return getAssociationById(associationId).getCouncilUserIds();
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
     * @param description description of the association
     * @param councilNumber add new association to the repo
     */
    public void addAssociation(String name, String description, int councilNumber) {
        associationRepository.save(new Association(name, description, councilNumber));
    }

    /**
     * Updates the association with the corresponding id.
     *
     * @param id                The association's id.
     * @param description       The description of the association.
     * @param councilUserIds    The set of user id's of the council members.
     */
    public void updateAssociation(int id, String description, HashSet<Integer> councilUserIds) {
        Association association = this.getAssociationById(id);
        association.setDescription(description);
        association.setCouncilUserIds(councilUserIds);
        associationRepository.save(association);
    }

    /**
     * Checks whether a certain user is part of the association's council.
     *
     * @param userId            The user's id.
     * @param associationId     The association id.
     * @return                  True if the user is part of the association's council.
     */
    public boolean verifyCouncilMember(Integer userId, Integer associationId) {
        if (userId == null || associationId == null) {
            return false;
        }

        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isPresent()) {
            Set<Integer> councilMembers = optionalAssociation.get().getCouncilUserIds();
            for (Integer i : councilMembers) {
                if (i.equals(userId)) {
                    return true;
                }
            }
        }
        return false;
    }

}
