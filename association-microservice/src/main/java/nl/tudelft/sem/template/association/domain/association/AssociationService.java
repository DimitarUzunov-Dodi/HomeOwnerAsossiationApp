package nl.tudelft.sem.template.association.domain.association;

import java.util.*;
import nl.tudelft.sem.template.association.domain.membership.Membership;
import nl.tudelft.sem.template.association.domain.membership.MembershipRepository;
import org.springframework.stereotype.Service;

@Service
public class AssociationService {
    private final transient AssociationRepository associationRepository;
    private final transient MembershipRepository membershipRepository;

    public AssociationService(AssociationRepository associationRepository, MembershipRepository membershipRepository) {
        this.associationRepository = associationRepository;
        this.membershipRepository = membershipRepository;
    }

    /**
     * Creates an association.
     *
     * @return a message confirming the creation.
     */
    public String createAssociation(String name, String country, String city, String description,
                                    int councilNumber) {
        Association association = new Association(name, country, city, description, councilNumber);
        int associationId = associationRepository.save(association).getId();
        return "Association was created:" + System.lineSeparator() + "ID: " + associationId + System.lineSeparator()
                + "Name: " + name + System.lineSeparator() + "Country: " + country + System.lineSeparator()
                + "Description: " + description + System.lineSeparator() + "Max council members: " + councilNumber;
    }

    /**
     * User joins an association.
     *
     * @return a message confirming the join.
     */
    public String joinAssociation(String userId, int associationId, String country, String city, String street,
                                  String houseNumber, String postalCode) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isEmpty()) {
            throw new IllegalArgumentException("Association with ID " + associationId + " does not exist.");
        }
        Association association = optionalAssociation.get();

        //Check if user has an address in the right city and country
        if (!association.getCity().equals(city) || !association.getCountry().equals(country)) {
            throw new IllegalArgumentException("You don't live in the right city or country to join this association.");
        }

        association.addMember(userId);
        Membership membership = new Membership(userId, associationId, country, city, street, houseNumber, postalCode);
        associationRepository.save(association);
        membershipRepository.save(membership);

        return "User " + userId + " successfully joined association " + associationId;
    }

    /**
     * User leaves an association.
     *
     * @return a message confirming the leave.
     */
    public String leaveAssociation(String userId, int associationId) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        Optional<Membership> optionalMembership = membershipRepository
                .findByUserIdAndAssociationIdAndLeaveDate(userId, associationId, null);
        if (optionalAssociation.isEmpty() || optionalMembership.isEmpty()) {
            throw new IllegalArgumentException("Association/membership does not exist.");
        }

        Association association = optionalAssociation.get();
        association.removeMember(userId);
        Membership membership = optionalMembership.get();
        membership.leave();
        associationRepository.save(association);
        membershipRepository.save(membership);
        return "User " + userId + " left association " + associationId;
    }

    /**
     * Updates the council in a specific association.
     *
     * <p>Checks for the following things:
     *  - the Association exists
     *  - the council size is allows
     *  - each council member is part of the association
     *
     * @param council the new council
     * @param associationId the id of the association
     */
    public void updateCouncil(Set<String> council, int associationId) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isEmpty()) {
            throw new IllegalArgumentException("Association does not exist");
        }

        Association association = optionalAssociation.get();

        if (council.size() > association.getCouncilNumber()) {
            throw new IllegalArgumentException("Council is bigger than allowed");
        }

        for (String councilMember : council) {
            if (!association.getMemberUserIds().contains(councilMember)) {
                throw new IllegalArgumentException("A Council member is not part of the association");
            }
        }

        association.setCouncilUserIds(council);
        associationRepository.save(association);
    }

    /**
     * Returns the council for a specific association.
     *
     * @param associationId the id of the association
     * @return the council
     */
    public Set<String> getCouncil(int associationId) {
        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isEmpty()) {
            throw new IllegalArgumentException("Association does not exist");
        }

        Association association = optionalAssociation.get();

        return association.getCouncilUserIds();
    }

    /**
     * Checks whether a certain user is part of the association's council.
     *
     * @param userId            The user's id.
     * @param associationId     The association id.
     * @return                  True if the user is part of the association's council.
     */
    public boolean verifyCouncilMember(String userId, Integer associationId) {
        if (userId == null || associationId == null) {
            return false;
        }

        Optional<Association> optionalAssociation = associationRepository.findById(associationId);
        if (optionalAssociation.isPresent()) {
            Set<String> councilMembers = optionalAssociation.get().getCouncilUserIds();
            for (String s : councilMembers) {
                if (s.equals(userId)) {
                    return true;
                }
            }
        }
        return false;
    }

}
