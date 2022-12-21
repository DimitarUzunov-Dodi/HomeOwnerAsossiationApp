package nl.tudelft.sem.template.association.domain.association;

import java.time.temporal.ChronoUnit;
import java.util.*;
import nl.tudelft.sem.template.association.domain.membership.*;
import nl.tudelft.sem.template.association.domain.user.*;
import org.springframework.stereotype.Service;

@Service
public class AssociationService {
    private final transient AssociationRepository associationRepository;
    private final transient UserRepository userRepository;
    private final transient MembershipRepository membershipRepository;

    /**
     * Instantiates a AssociationService object which provides methods to the Association endpoints,
     * while handling the databases.
     */
    public AssociationService(AssociationRepository associationRepository, UserRepository userRepository,
                              MembershipRepository membershipRepository) {
        this.associationRepository = associationRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
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

    /**
     * Verify whether the provided user can be a candidate for the board.
     *
     * @param userId            The user's id.
     * @param associationId     The association id.
     * @return                  True if the user can be a candidate.
     */
    public boolean verifyCandidate(Integer userId, Integer associationId) {
        if (userId == null || associationId == null) {
            return false;
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return false;
        }
        String userIdString = optionalUser.get().getUserId();

        List<Membership> memberships = membershipRepository.findAllByUserId(userIdString);

        //Check for council membership in all of user's associations
        for (Membership m : memberships) {
            Optional<Association> optionalAssociation = associationRepository.findById(m.getAssociationId());
            if (optionalAssociation.isEmpty() || optionalAssociation.get().getCouncilUserIds().contains(userId)) {
                return false;
            }
        }

        //Check if the member has been in the HOA for 3 years
        Optional<Membership> optionalMembership = membershipRepository
                .findByUserIdAndAssociationIdAndLeaveDate(userIdString, associationId, null);
        if (optionalMembership.isEmpty()) {
            return false;
        }

        Date currentDate = new Date(System.currentTimeMillis());
        Date joinDate = optionalMembership.get().getJoinDate();
        Long candidateYearLimit = 3L;
        if (ChronoUnit.YEARS.between(joinDate.toInstant(), currentDate.toInstant()) < candidateYearLimit) {
            return false;
        }

        //TODO: Check for 10 year board membership

        return true;
    }

}
